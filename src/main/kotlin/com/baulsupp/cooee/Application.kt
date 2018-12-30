package com.baulsupp.cooee

import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.ktor.application.Application
import io.ktor.application.ApplicationStopped
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DataConversion
import io.ktor.features.HttpsRedirect
import io.ktor.features.gzip
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.server.engine.ShutDownUrl
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.conscrypt.Conscrypt
import java.security.Security
import java.util.*

@KtorExperimentalLocationsAPI
fun main(args: Array<String>) {
  // TODO breaks Mongo
//  setupProvider()

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

fun Application.local() = module(ProdAppServices(true))
fun Application.cloud() = module(ProdAppServices(false), false)

@KtorExperimentalLocationsAPI
@kotlin.jvm.JvmOverloads
fun Application.module(appServices: AppServices, local: Boolean = true) {
  this.environment.monitor.subscribe(ApplicationStopped) {
    appServices.close()
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
  install(DataConversion)
  install(AutoHeadResponse)

  if (!local) {
    install(HttpsRedirect) {
      sslPort = 443
      permanentRedirect = true
    }
  }

  if (local) {
    install(ShutDownUrl.ApplicationCallFeature) {
      shutDownUrl = "/ktor/application/shutdown"
      exitCodeSupplier = { 0 }
    }
  }

  routing {
    if (local) {
      trace { application.log.trace(it.buildText()) }
    }

    root(appServices)
  }
}

private fun setupProvider() {
  try {
    Security.insertProviderAt(Conscrypt.newProviderBuilder().provideTrustManager().build(), 1)
  } catch (e: NoClassDefFoundError) {
    // Drop back to JDK
  }
}
