package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.*
import com.squareup.moshi.Moshi
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller("completion")
class CompletionController(val moshi: Moshi) {
  @MessageMapping("complete")
  suspend fun requestResponse(request: String): String {
    val requestCommand = moshi.adapter(CompletionRequest::class.java).fromJson(request)!!
    val response = executeRequest(requestCommand)
    return moshi.adapter(CompletionResponse::class.java).toJson(response)
  }

  private fun executeRequest(requestCommand: CompletionRequest) =
      CompletionResponse(completions = listOf(CompletionSuggestion(line = requestCommand.command + " A")))
}