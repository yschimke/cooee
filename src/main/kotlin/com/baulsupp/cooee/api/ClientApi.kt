package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.TokenResponse

interface ClientApi {
  suspend fun tokenRequest(request: TokenRequest): TokenResponse

  suspend fun logToClient(log: LogRequest): Unit
}