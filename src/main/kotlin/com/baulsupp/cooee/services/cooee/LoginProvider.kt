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
import com.baulsupp.okurl.authenticator.oauth2.Oauth2ServiceDefinition
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
