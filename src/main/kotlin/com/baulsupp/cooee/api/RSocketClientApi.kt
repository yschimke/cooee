package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionResponse
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.TokenResponse
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.messaging.rsocket.retrieveFlow
import org.springframework.messaging.rsocket.sendAndAwait

class RSocketClientApi(private val rSocketRequester: RSocketRequester): ClientApi {
  override suspend fun tokenRequest(request: TokenRequest) =
      rSocketRequester.route("token").data(request).retrieveAndAwait<TokenResponse>()

  override suspend fun logToClient(log: LogRequest) =
      rSocketRequester.route("log").data(log).sendAndAwait()

  override suspend fun commandRequest(request: CommandRequest) =
    rSocketRequester.route("runCommand").data(request).retrieveFlow<CommandResponse>()

  override suspend fun complete(request: CompletionRequest) =
    rSocketRequester.route("complete").data(request).retrieveAndAwait<CompletionResponse>()

  companion object {
    fun RSocketRequester.asClientApi() = RSocketClientApi(this)
  }
}