package com.baulsupp.cooee.services.cooee

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandSuggestion
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.p.command
import com.baulsupp.cooee.p.done
import com.baulsupp.cooee.p.redirect
import com.baulsupp.cooee.services.Provider
import com.baulsupp.okurl.authenticator.oauth2.Oauth2ServiceDefinition
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor

class CooeeProvider : Provider("cooee") {
  override suspend fun runCommand(request: CommandRequest): CommandResponse? = when {
    request.parsed_command == listOf("cooee") -> cooeeWebsite
    request.parsed_command.getOrNull(1) == "login" -> CommandResponse.done("Logged In")
    request.parsed_command.getOrNull(1) == "logout" -> CommandResponse.done("Logged Out")
    else -> null
  }

  override suspend fun matches(command: String): Boolean {
    return command == "cooee"
  }

  override suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> {
    return listOf(
        CompletionSuggestion.command(CommandSuggestion(provider = "cooee", description = "Coo.ee Website"), "cooee"),
        CompletionSuggestion.command(CommandSuggestion(provider = "cooee", description = "Login to Coo.ee"), "cooee", "login"),
        CompletionSuggestion.command(CommandSuggestion(provider = "cooee", description = "Logout from Coo.ee"), "cooee", "logout")
    )
  }

  companion object {
    val serviceDefinition = Oauth2ServiceDefinition("api.coo.ee", "Coo.ee", "cooee")
    val cooeeWebsite = CommandResponse.redirect("https://www.coo.ee/")
  }
}
