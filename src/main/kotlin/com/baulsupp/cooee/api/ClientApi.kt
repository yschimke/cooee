package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.TokenResponse
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwait

class ClientApi(val rSocketRequester: RSocketRequester) {
  suspend fun tokenRequest(service: String) =
      rSocketRequester.route("token").data(TokenRequest(service = service)).retrieveAndAwait<TokenResponse>()
}