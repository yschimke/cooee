@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.baulsupp.cooee

import io.ktor.config.MapApplicationConfig
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes

@KtorExperimentalLocationsAPI
@ExperimentalCoroutinesApi
fun main() {
  DebugProbes.install()

  val env = applicationEngineEnvironment {
    module {
      module(ProdAppServices(this), cloud = false)
    }
    config = MapApplicationConfig(
      "apiHost" to "localhost:8080",
      "wwwwHost" to "localhost:3000"
    )
    config.config("host")
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
