package com.baulsupp.cooee.api

import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.*
import com.baulsupp.cooee.services.CombinedProvider
import okhttp3.OkHttpClient
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.stereotype.Controller

@Controller
class CommandController(val client: OkHttpClient, val combinedProvider: CombinedProvider, val cache: LocalCache) {
  @MessageMapping("runCommand")
  suspend fun runCommand(request: CommandRequest, rSocketRequester: RSocketRequester): CommandResponse {
    combinedProvider.init(client, ClientApi(rSocketRequester), cache)

    val response = combinedProvider.runCommand(request)

    return response ?: CommandResponse.unmatched()
  }
}
