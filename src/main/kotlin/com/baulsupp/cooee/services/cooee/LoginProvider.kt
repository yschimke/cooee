package com.baulsupp.cooee.services.cooee

import com.baulsupp.cooee.cache.AuthFlowCache
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
import com.baulsupp.cooee.p.single_command
import com.baulsupp.cooee.services.Provider
import com.baulsupp.cooee.services.twitter.TwitterAuthFlow
import com.baulsupp.okurl.authenticator.AuthInterceptor
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.authenticator.authflow.*
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Flow
import com.baulsupp.okurl.authenticator.oauth2.Oauth2ServiceDefinition
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.IllegalStateException
import java.util.*

class LoginProvider(val authFlowCache: AuthFlowCache) : Provider("login") {
  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    val service = request.parsed_command.getOrNull(1)

    return when (request.single_command) {
      "login" -> flow { emit(loginCooee(service)) }
      "logout" -> flow { emit(logout(service)) }
      else -> null
    }
  }

  suspend fun loginCooee(service: String?): CommandResponse {
    return if (service == null) {
      loginCooee()
    } else {
      loginService(service)
    }
  }

  val config = mapOf(
      "strava.clientId" to "31260",
  )

  private fun optionParams(serviceFlow: Oauth2Flow<*>, state: String): Map<String, Any> {
    val options = serviceFlow.options()

//    println(options.map { it.param })

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
    return coroutineScope {
      val serviceDefinition = AuthenticatingInterceptor.defaultServices().find {
        it.serviceDefinition.shortName() == service
      } ?: throw IllegalStateException("unknown service $service")

      authorize(serviceDefinition)

      CommandResponse.done("authed")
    }
  }

  suspend fun <T> authorize(authInterceptor: AuthInterceptor<T>) {
    val service = authInterceptor.serviceDefinition.shortName()
    val flow: AuthFlow<T> = authFlow(authInterceptor, service)
    flow.init(client)

    val state = UUID.randomUUID().toString()

    val params = optionParams(flow as Oauth2Flow<*>, state)
    flow.defineOptions(params)

    val url = flow.start()
//    println(url)

    val result = authFlowCache.get(state)

    val authFlowResponse =
        clientApi.tokenRequest(TokenRequest(service = service, login_url = url))

    val code = result.await()
//    println(code)

    val newToken: T = flow.complete(code) as T
//    println(newToken)

    val tokenString = authInterceptor.serviceDefinition.formatCredentialsString(newToken)

    val tokenResponse = clientApi.tokenRequest(TokenRequest(service = service, token = tokenString))
  }

  private fun <T> authFlow(
    authInterceptor: AuthInterceptor<T>,
    service: String
  ): AuthFlow<T> {
    if (service == "twitter") {
      return TwitterAuthFlow(authFlowCache) as AuthFlow<T>
    }

    return authInterceptor.authFlow() ?: throw IllegalStateException(
        "unknown auth flow $service")
  }

  suspend fun loginCooee(): CommandResponse {
    val tokenResponse = clientApi.tokenRequest(TokenRequest(service = "cooee",
        login_url = "https://www.coo.ee/user/jwt?callback=http://localhost:3000/callback"))

    return when {
      tokenResponse.login_attempted -> {
        CommandResponse.done("Logged In (fresh auth)")
      }
      tokenResponse.token?.token != null -> {
        CommandResponse.done("Logged In Already (client token)")
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
