package com.baulsupp.cooee.services

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.p.TodoResponse

interface ProviderFunctions {
  suspend fun runCommand(request: CommandRequest): CommandResponse? = null
  suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> = listOf()
  suspend fun todo(): TodoResponse? = null
}
