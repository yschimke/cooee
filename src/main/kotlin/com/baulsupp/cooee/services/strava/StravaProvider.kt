package com.baulsupp.cooee.services.strava

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.redirect
import com.baulsupp.cooee.services.Provider

class StravaProvider : Provider("strava") {
  override suspend fun runCommand(request: CommandRequest): CommandResponse? = when {
    request.parsed_command == listOf("strava") -> CommandResponse.redirect("https://www.strava.com/")
    request.parsed_command.getOrNull(1) == "lastrun" -> lastRun(client, clientApi)
    else -> null
  }

  override suspend fun matches(command: String): Boolean {
    return command == "strava"
  }
}
