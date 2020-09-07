package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.TokenResponse
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.messaging.rsocket.sendAndAwait

class ClientApi(val rSocketRequester: RSocketRequester) {
  suspend fun tokenRequest(service: String) =
      rSocketRequester.route("token").data(TokenRequest(service = service)).retrieveAndAwait<TokenResponse>()

  suspend fun logToClient(log: LogRequest) =
      rSocketRequester.route("log").data(log).sendAndAwait()
}