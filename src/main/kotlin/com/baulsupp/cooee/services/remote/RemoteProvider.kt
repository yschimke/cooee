package com.baulsupp.cooee.services.remote

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.services.Provider
import kotlinx.coroutines.flow.Flow

class RemoteProvider: Provider("remote") {
  val servers = mutableListOf<RemoteProviderInstance>()

  override suspend fun matches(command: String): Boolean {
    return servers.find { it.commandName == command } != null
  }

  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    val server = servers.find { it.commandName == request.parsed_command.firstOrNull() }

    return server?.runCommand(request)
  }

  override suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> {
    // TODO async
    return servers.flatMap { it.suggest(command) }
  }
}