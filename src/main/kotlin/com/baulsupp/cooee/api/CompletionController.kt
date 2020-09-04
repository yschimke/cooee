package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.*
import com.baulsupp.cooee.services.CombinedProvider
import com.squareup.moshi.Moshi
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class CompletionController(val combinedProvider: CombinedProvider) {
  @MessageMapping("complete")
  suspend fun complete(request: CompletionRequest): CompletionResponse {
    return CompletionResponse(completions = listOf(
        CompletionSuggestion(line = request.line + " A"),
        CompletionSuggestion(line = request.line + " B")
    )
    )
  }
}