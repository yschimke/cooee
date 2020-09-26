package com.baulsupp.cooee.services.cooee

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandSuggestion
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.command
import com.baulsupp.cooee.p.done
import com.baulsupp.cooee.p.error
import com.baulsupp.cooee.p.prefix
import com.baulsupp.cooee.p.redirect
import com.baulsupp.cooee.p.single_command
import com.baulsupp.cooee.services.Provider
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.authenticator.authflow.*
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Flow
import com.baulsupp.okurl.authenticator.oauth2.Oauth2ServiceDefinition
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class LoginProvider : Provider("login") {
  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    val service = request.parsed_command.getOrNull(1)

    return when (request.single_command) {
      "login" -> flow { emit(login(service)) }
      "logout" -> flow { emit(logout(service)) }
      else -> null
    }
  }

  suspend fun login(service: String?): CommandResponse {
    return if (service == null) {
      login()
    } else {
      loginService(service)
    }
  }

  val config = mapOf("" to ""
  )

  private fun optionParams(serviceFlow: Oauth2Flow<*>, state: String): Map<String, Any> {
    val options = serviceFlow.options()

    println(options.map { it.param })

    return options.map {
      val value: Any = when (it) {
        is Prompt -> config[it.param] ?: ""
        is Scopes -> config[it.param]?.split(",") ?: it.known
        is Callback -> "http://localhost:8080/callback"
        is State -> state
      }

      it.param to value
    }.toMap()
  }

  suspend fun loginService(service: String): CommandResponse {
    val flow = authFlow(service) ?: return CommandResponse.error("not found")

    flow.init(client)

    val state = UUID.randomUUID().toString()

    val params = optionParams(flow as Oauth2Flow<*>, state)
    flow.defineOptions(params)
//    appServices.authenticationFlowCache.store(AuthenticationFlowInstance(state, token, request.service))

    val url = flow.start()

    clientApi.tokenRequest(TokenRequest(service = service, login_url = url))

    println(url)

    return CommandResponse.done("authed")
  }

  private fun authFlow(service: String): AuthFlow<out Any?>? {
    return AuthenticatingInterceptor.defaultServices().find {
      it.serviceDefinition.shortName() == service
    }?.authFlow()
  }

  suspend fun login(): CommandResponse {
    val tokenResponse = clientApi.tokenRequest(TokenRequest(service = "cooee",
        login_url = "https://www.coo.ee/user/jwt?callback=http://localhost:3000/callback"))

    return when {
      tokenResponse.login_attempted -> {
        CommandResponse.done("Logged In (fresh auth)")
      }
      tokenResponse.token?.token != null -> {
        CommandResponse.done("Logged In (client token)")
      }
      else -> {
        CommandResponse.error("Login failed")
      }
    }
  }

  fun logout(service: String?): CommandResponse {
    return CommandResponse.done("Logged Out")
  }

  override suspend fun matches(command: String): Boolean {
    return command == "login" || command == "logout"
  }

  override suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> {
    return listOf(
        CompletionSuggestion.command(CommandSuggestion(provider = "cooee", description = "Login to Coo.ee"), "cooee", "login"),
        CompletionSuggestion.command(CommandSuggestion(provider = "cooee", description = "Logout from Coo.ee"), "cooee", "logout"),
        CompletionSuggestion.prefix(provider = "cooee", "login"),
        CompletionSuggestion.prefix(provider = "cooee", "logout"),
    )
  }

  companion object {
    val serviceDefinition = Oauth2ServiceDefinition("api.coo.ee", "Coo.ee", "cooee")
  }
}
