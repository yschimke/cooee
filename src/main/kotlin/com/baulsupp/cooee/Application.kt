package com.baulsupp.cooee

import com.baulsupp.cooee.api.Go
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.*
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import kotlinx.css.CSSBuilder
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.FlowOrMetaDataContent
import kotlinx.html.style
import io.ktor.application.log
import java.time.Duration
import java.util.*
import kotlin.collections.set

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

  install(ShutDownUrl.ApplicationCallFeature) {
    shutDownUrl = "/ktor/application/shutdown"
    exitCodeSupplier = { 0 }
  }

  routing {
    if (testing) {
      trace { application.log.trace(it.buildText()) }
    }

//    get("/") {
//      call.respondText("Cooee!", contentType = ContentType.Text.Plain)
//    }

    get<Go> { location ->
      call.respondRedirect("https://google.com?q=" + location.q, permanent = false)
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
