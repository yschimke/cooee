package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionResponse
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.TokenResponse
import kotlinx.coroutines.flow.Flow

interface ClientApi {
  suspend fun tokenRequest(request: TokenRequest): TokenResponse

  suspend fun logToClient(log: LogRequest)

  suspend fun commandRequest(request: CommandRequest): Flow<CommandResponse>

  suspend fun complete(request: CompletionRequest): CompletionResponse
}