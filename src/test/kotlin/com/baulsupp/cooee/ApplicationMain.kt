package com.baulsupp.cooee

import io.ktor.application.Application
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes

@KtorExperimentalLocationsAPI
fun Application.local() = module(ProdAppServices(this), cloud = false)

@KtorExperimentalLocationsAPI
@ExperimentalCoroutinesApi
fun main(args: Array<String>) {
  DebugProbes.install()

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
