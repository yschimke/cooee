package com.baulsupp.cooee

import com.baulsupp.cooee.api.Go
import com.baulsupp.cooee.api.GoInfo
import com.baulsupp.cooee.providers.RedirectResult
import com.baulsupp.cooee.providers.RegistryProvider
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.honeycomb.libhoney.HoneyClient
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.css.CSSBuilder
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.FlowOrMetaDataContent
import kotlinx.html.style
import java.util.*
import io.honeycomb.libhoney.LibHoney.create
import io.honeycomb.libhoney.LibHoney.options
import java.net.InetAddress
import java.util.UUID.randomUUID



@KtorExperimentalLocationsAPI
fun main(args: Array<String>) {
  val env = applicationEngineEnvironment {
    module {
      test()
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

fun Application.test() = module(true)
fun Application.main() = module(false)

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
//  val client = HttpClient(OkHttp) {
//    install(JsonFeature) {
//      serializer = GsonSerializer()
//    }
//  }

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

  install(CORS)
  install(CallLogging)
  install(DataConversion)
  install(AutoHeadResponse)

  if (!testing) {
    install(HttpsRedirect) {
      // The port to redirect to. By default 443, the default HTTPS port.
      sslPort = 443
      // 301 Moved Permanently, or 302 Found redirect.
      permanentRedirect = true
    }
  }

  if (!testing) {
    honeyClient = create(
      options()
        .setWriteKey("e74690c0b31c1a029944d107e825cff3")
        .setDataset("java")
        .build()
    )

    sendStartupInfo()
  }

  if (testing) {
    install(ShutDownUrl.ApplicationCallFeature) {
      shutDownUrl = "/ktor/application/shutdown"
      exitCodeSupplier = { 0 }
    }
  }

  routing {
    if (testing) {
      trace { application.log.trace(it.buildText()) }
    }

    get<Go> { location ->
      val r =
        location.command?.let { RegistryProvider.url(location.command, location.args) } ?: RedirectResult.UNMATCHED

      if (r.location != null) {
          call.respondRedirect(r.location, permanent = false)
      } else {
          call.respond(HttpStatusCode.NotFound)
      }
    }

    get<GoInfo> { location ->
      val r =
        location.command?.let { RegistryProvider.url(location.command, location.args) } ?: RedirectResult.UNMATCHED

      call.respond(r)
    }

    install(StatusPages) {
      exception<AuthenticationException> { cause ->
        call.respond(HttpStatusCode.Unauthorized)
      }
      exception<AuthorizationException> { cause ->
        call.respond(HttpStatusCode.Forbidden)
      }
    }

    static {
      resources("static")
      defaultResource("static/index.html")
    }
  }
}

fun sendStartupInfo() {
  val dataMap = mutableMapOf<String, Any>()
  dataMap.put("randomString", UUID.randomUUID().toString())
  dataMap.put("cpuCores", Runtime.getRuntime().availableProcessors())
  dataMap.put("hostname", InetAddress.getLocalHost().hostName)

  honeyClient.use { honeyClient -> honeyClient.send(dataMap) }
}

lateinit var honeyClient: HoneyClient

data class MySession(val count: Int = 0)

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
  style(type = ContentType.Text.CSS.toString()) {
    +CSSBuilder().apply(builder).toString()
  }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
  this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
  this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
