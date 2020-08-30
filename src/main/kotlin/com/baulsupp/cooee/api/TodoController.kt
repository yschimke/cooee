package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.*
import com.squareup.moshi.Moshi
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller("todo")
class TodoController(val moshi: Moshi) {
  @MessageMapping("todo")
  suspend fun requestStream(request: String): String {
    val requestCommand = moshi.adapter(TodoRequest::class.java).fromJson(request)!!
    val response = executeRequest(requestCommand)
    return moshi.adapter(TodoResponse::class.java).toJson(response)
  }

  private fun executeRequest(requestCommand: TodoRequest) =
      TodoResponse(todos = listOf(CompletionSuggestion("todo A"), CompletionSuggestion("todo B")))
}