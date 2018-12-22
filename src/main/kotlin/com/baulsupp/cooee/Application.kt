package com.baulsupp.cooee

import com.baulsupp.cooee.api.*
import com.baulsupp.cooee.ktor.AccessLogs
import com.baulsupp.cooee.mongo.MongoFactory
import com.baulsupp.cooee.mongo.MongoProviderStore
import com.baulsupp.cooee.mongo.MongoUserStore
import com.baulsupp.cooee.okhttp.HoneycombEventListenerFactory
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.TestProviderStore
import com.baulsupp.cooee.providers.defaultProviders
import com.baulsupp.cooee.users.JwtUserAuthenticator
import com.baulsupp.cooee.users.TestUserAuthenticator
import com.baulsupp.cooee.users.TestUserStore
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.cooee.users.UserStore
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.honeycomb.libhoney.HoneyClient
import io.honeycomb.libhoney.LibHoney.create
import io.honeycomb.libhoney.LibHoney.options
import io.honeycomb.libhoney.ValueSupplier
import io.jsonwebtoken.JwtException
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationStopped
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DataConversion
import io.ktor.features.HttpsRedirect
import io.ktor.features.StatusPages
import io.ktor.features.gzip
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.routing
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.pipeline.PipelineContext
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import org.conscrypt.Conscrypt
import java.lang.management.ManagementFactory
import java.net.InetAddress
import java.security.Security
import java.time.Duration
import java.util.*
import kotlin.concurrent.timer

@KtorExperimentalLocationsAPI
fun main(args: Array<String>) {
  setupProvider()

  val env = applicationEngineEnvironment {
    module {
      local()
    }
    // Private API
    connector {
      host = "127.0.0.1"
      port = 9090
    }
    // Public API
    connector {
      host = "0.0.0.0"
      port = 8080
    }
  }
  embeddedServer(Netty, env).start(true)
}

fun Application.test() = module(true, true)
fun Application.local() = module(false, true)
fun Application.cloud() = module(false, false)

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false, local: Boolean = false) {
  MongoFactory.local = local

  this.environment.monitor.subscribe(ApplicationStopped) {
    MongoFactory.close()
  }

  install(ContentNegotiation) {
    moshi {
      add(Date::class.java, Rfc3339DateJsonAdapter())
    }
  }

  install(Locations)

  install(Compression) {
    gzip {
      priority = 1.0
    }
  }

  install(CORS) {
    anyHost()
  }
  install(CallLogging)
  install(AccessLogs)
  install(DataConversion)
  install(AutoHeadResponse)

  if (!local) {
    enforceHttps()
  }

  if (!local) {
    createHoneyClient()

    timer(name = "uptime", daemon = true, initialDelay = 0, period = Duration.ofMinutes(15).toMillis()) {
      sendUptime()
    }
  }

  if (local) {
    install(ShutDownUrl.ApplicationCallFeature) {
      shutDownUrl = "/ktor/application/shutdown"
      exitCodeSupplier = { 0 }
    }
  }

  val httpListener = if (local) {
    LoggingEventListener.Factory { s -> println(s) }
  } else HoneycombEventListenerFactory(
    honeyClient!!
  )

  val client = buildHttpClient(httpListener)

  val defaultProvider = RegistryProvider(defaultProviders(client))

  val providerStore = when {
    testing -> TestProviderStore()
    else -> MongoProviderStore(defaultProvider)
  }

  val userStore = when {
    testing -> TestUserStore()
    else -> MongoUserStore()
  }

  val userAuthenticator = when {
    testing -> TestUserAuthenticator()
    else -> JwtUserAuthenticator(userStore)
  }

  suspend fun providersFor(call: ApplicationCall): RegistryProvider =
    userAuthenticator.userForRequest(call)?.let { providerStore.forUser(it) } ?: defaultProvider

  routing {
    if (local) {
      trace { application.log.trace(it.buildText()) }
    }

    get<Login> { loginWeb(it, userStore) }
    get<Go> { bounceWeb(it, providersFor(call)) }
    get<GoInfo> { bounceApi(it, providersFor(call)) }
    get<UserInfo> { userApi(userAuthenticator.userForRequest(call), userStore) }
    get<CommandCompletion> { commandCompletionApi(it, providersFor(call)) }
    get<ArgumentCompletion> { argumentCompletionApi(it, providersFor(call)) }

    install(StatusPages) {
      exception<JwtException> { cause ->
        application.log.warn("jwt error", cause)
        call.respond(HttpStatusCode.BadRequest)
      }
      exception<AuthenticationException> { call.respond(HttpStatusCode.Unauthorized) }
      exception<AuthorizationException> { call.respond(HttpStatusCode.Forbidden) }
      exception<Exception> { x -> call.respond(HttpStatusCode.InternalServerError, x.toString()) }
    }

    static {
      resources("static")
      defaultResource("static/index.html")
    }
  }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.bounceApi(
  goInfo: GoInfo,
  registryProvider: RegistryProvider
) {
  val r =
    goInfo.command?.let { registryProvider.url(it, goInfo.args) } ?: Unmatched

  call.respond(r)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.userApi(
  userToken: String?,
  userStore: UserStore
) {
  if (userToken != null) {
    val userResult = userStore.userInfo(userToken)

    if (userResult != null) {
      call.respond(userResult)
    }
  }

  call.respond(HttpStatusCode.Unauthorized)
}

@KtorExperimentalLocationsAPI
private suspend fun PipelineContext<Unit, ApplicationCall>.loginWeb(
  login: Login,
  userStore: UserStore
) {
  if (login.callback != null && login.user != null) {
    val token = JwtUserAuthenticator.tokenForLogin(login)

    if (token != null) {
      userStore.storeUser(UserEntry(token, login.user, login.email))
      call.respondRedirect(login.callback + "?code=" + token, permanent = false)
    }
  }

  call.respond(HttpStatusCode.Unauthorized)
}

private suspend fun PipelineContext<Unit, ApplicationCall>.bounceWeb(
  go: Go,
  registryProvider: RegistryProvider
) {
  val r =
    go.command?.let { registryProvider.url(it, go.args) } ?: Unmatched

  when (r) {
    is RedirectResult -> call.respondRedirect(r.location, permanent = false)
    is Unmatched -> call.respond(HttpStatusCode.NotFound)
    is Completed -> call.respond(HttpStatusCode.NoContent)
  }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.commandCompletionApi(
  commandQuery: CommandCompletion,
  registryProvider: RegistryProvider
) {
  val command = commandQuery.q ?: ""

  val completions = registryProvider.commandCompleter().suggestCommands(command)

  // TODO not needed
  val filtered = completions.filter { it.startsWith(command) }

  call.respond(Completions(filtered))
}

private suspend fun PipelineContext<Unit, ApplicationCall>.argumentCompletionApi(
  argumentQuery: ArgumentCompletion,
  registryProvider: RegistryProvider
) {
  call.respond(Completions(listOf("close", "comment")))
}

private fun buildHttpClient(httpListener: EventListener.Factory): OkHttpClient {
  return OkHttpClient.Builder().eventListenerFactory(httpListener).build()
}

private fun createHoneyClient() {
  val dataMap = mutableMapOf<String, Any>()
  dataMap.put("cpuCores", Runtime.getRuntime().availableProcessors())
  dataMap.put("instance", UUID.randomUUID().toString())
  dataMap.put("hostname", InetAddress.getLocalHost().hostName)

  val dynamicFields = mapOf("uptime" to ValueSupplier<Long> { ManagementFactory.getRuntimeMXBean().uptime })

  honeyClient = create(
    options()
      .setWriteKey("e74690c0b31c1a029944d107e825cff3")
      .setDataset("java")
      .setGlobalFields(dataMap)
      .setGlobalDynamicFields(dynamicFields)
      .build()
  )
}

private fun Application.enforceHttps() {
  install(HttpsRedirect) {
    // The port to redirect to. By default 443, the default HTTPS port.
    sslPort = 443
    // 301 Moved Permanently, or 302 Found redirect.
    permanentRedirect = true
  }
}

fun sendUptime() {
  honeyClient?.createEvent()?.setDataset("uptime")?.send()
}

var honeyClient: HoneyClient? = null

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

private fun setupProvider() {
  try {
    Security.insertProviderAt(Conscrypt.newProviderBuilder().provideTrustManager().build(), 1)
  } catch (e: NoClassDefFoundError) {
    // Drop back to JDK
  }
}
