package com.baulsupp.cooee.api

import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionResponse
import com.baulsupp.cooee.services.CombinedProvider
import okhttp3.OkHttpClient
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Controller

@Controller
class CompletionController(val client: OkHttpClient, val combinedProvider: CombinedProvider, val cache: LocalCache) {
  @MessageMapping("complete")
  suspend fun complete(request: CompletionRequest, rSocketRequester: RSocketRequester): CompletionResponse {
    val clientApi = ClientApi(rSocketRequester)

    combinedProvider.init(client, clientApi, cache)

    val response = combinedProvider.suggest(request)

    return CompletionResponse(response)
  }
}