package com.baulsupp.cooee.services

import com.baulsupp.cooee.p.*

interface ProviderFunctions {
  suspend fun runCommand(request: CommandRequest): CommandResponse? = null
  suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> = listOf()
  suspend fun todo(): TodoResponse? = null
}
