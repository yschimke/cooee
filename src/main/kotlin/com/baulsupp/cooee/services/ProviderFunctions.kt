package com.baulsupp.cooee.services

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import kotlinx.coroutines.flow.Flow

interface ProviderFunctions {
  suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? = null
  suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> = listOf()
}
