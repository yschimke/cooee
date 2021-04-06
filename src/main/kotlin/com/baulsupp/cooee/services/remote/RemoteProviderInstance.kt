package com.baulsupp.cooee.services.remote

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.services.Provider
import kotlinx.coroutines.flow.Flow

class RemoteProviderInstance(val commandName: String, val serverApi: ClientApi): Provider(commandName) {
  override suspend fun matches(command: String): Boolean {
    return command == commandName
  }

  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse> {
    return serverApi.commandRequest(request)
  }

  override suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> {
    return serverApi.complete(command).completions
  }
}