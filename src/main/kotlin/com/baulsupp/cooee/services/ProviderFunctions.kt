package com.baulsupp.cooee.services

import com.baulsupp.cooee.p.*

interface ProviderFunctions {
  suspend fun runCommand(request: CommandRequest): CommandResponse? = null
  suspend fun suggest(command: CompletionRequest): CompletionResponse? = null
  suspend fun todo(): TodoResponse? = null
}
