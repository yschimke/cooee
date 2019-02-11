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
      "wwwwHost" to "localhost:3000",
      "strava.clientId" to "31260",
      "strava.clientSecret" to "0cf3ecbf9d82ac4e190c0ffbf634e720c2853c63",
      "github.clientId" to "55437c08a32bb9838c51",
      "github.clientSecret" to "73c01e814183a59a2d7c01791a75ca4db25abc0d",
      "atlassian.clientId" to "3XE2L6KCIUmcYZXxzgu3XXPms0rBMvOb",
      "atlassian.clientSecret" to "3CQ7XqG-FKC_G14pYE_Rf2-dw80aw9k_EyIr9NxpgYJTnMBq2qXbWAd_dBqAfsd6"
    ).apply {
      put(
        "strava.scopes", listOf(
          "read_all",
          "profile:read_all",
          "profile:write",
          "activity:read_all",
          "activity:write"
        )
      )
      put(
        "github.scopes", listOf(
          "user", "repo", "gist", "admin:org"
        )
      )
      put(
        "atlassian.scopes", listOf(
          "read:jira-user", "read:jira-work", "write:jira-work", "offline_access"
        )
      )
    }


//    atlassian {
//      clientId = lWE2rprvJ7rcRrV28e7CUg2ErITtyw0p
//      clientSecret = CqcwVFKugiK1AhP-HcRG26BwgdhML7-_Pa2UvVM5qjS8tD_xh50MCRVhFAPI24_Q
//      scopes = [
//        read:jira-user,
//      read:jira-work,
//      write:jira-work,
//      offline_access
//      ]
//    }
//    github {
//      clientId = 55437c08a32bb9838c51
//        clientSecret = 73c01e814183a59a2d7c01791a75ca4db25abc0d
//        scopes = [
//        user,
//        repo,
//        gist,
//        admin:org
//      ]
//    }


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
