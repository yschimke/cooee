package com.baulsupp.cooee.services.strava

import com.baulsupp.cooee.p.*
import com.baulsupp.cooee.services.Provider

class StravaProvider : Provider("strava") {
  override suspend fun runCommand(request: CommandRequest): CommandResponse? = when {
    request.parsed_command == listOf("strava") -> stravaWebsite
    request.parsed_command.getOrNull(1) == "last" -> lastRun(client, clientApi)
    else -> null
  }

  override suspend fun matches(command: String): Boolean {
    return command == "strava"
  }

  override suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> {
    return listOf(
        CompletionSuggestion.command(CommandSuggestion(provider = "strava", description = "Strava Website"), "strava"),
        CompletionSuggestion.command(CommandSuggestion(provider = "strava", description = "Last Activity in Strava"), "strava", "last")
    )
  }

  companion object {
    val stravaWebsite = CommandResponse.redirect("https://www.strava.com/")
  }
}