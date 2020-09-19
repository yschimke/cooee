package com.baulsupp.cooee.api

import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.error
import com.baulsupp.cooee.p.unmatched
import com.baulsupp.cooee.services.CombinedProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onErrorReturn
import kotlinx.coroutines.flow.toList
import okhttp3.OkHttpClient
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class CommandController(val client: OkHttpClient, val combinedProvider: CombinedProvider, val cache: LocalCache) {
  @MessageMapping("runCommand")
  suspend fun runCommand(request: CommandRequest, rSocketRequester: RSocketRequester, @AuthenticationPrincipal jwt: String): Flow<CommandResponse> {
    val clientApi = ClientApi(rSocketRequester)

    combinedProvider.init(client, clientApi, cache)

    val response = combinedProvider.runCommand(request)

    val flow = response ?: flowOf(CommandResponse.unmatched())

    return flow.catch {
      e -> emit(CommandResponse.error(e.message ?: e.toString()))
    }
  }
}
