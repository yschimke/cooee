package com.baulsupp.cooee.api

import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.*
import com.baulsupp.cooee.services.CombinedProvider
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Controller

@Controller
class CompletionController(val client: OkHttpClient, val combinedProvider: CombinedProvider, val cache: LocalCache) {
  @MessageMapping("complete")
  suspend fun complete(request: CompletionRequest, rSocketRequester: RSocketRequester): CompletionResponse {
    println(request)

    val clientApi = ClientApi(rSocketRequester)

    combinedProvider.init(client, clientApi, cache)

    val response = combinedProvider.suggest(request)

    println(response)

    return CompletionResponse(response)
  }
}