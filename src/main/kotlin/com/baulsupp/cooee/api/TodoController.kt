package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller("todo")
class TodoController(val moshi: Moshi) {
  @MessageMapping("todo")
  suspend fun todo(request: TodoRequest): Flow<TodoResponse> {
    return flow {
      emit(TodoResponse(todos = listOf(
          CommandSuggestion("todo A"),
          CommandSuggestion("todo B"))
      ))

      delay(2500)

      emit(TodoResponse(todos = listOf(
          CommandSuggestion("todo C"),
          CommandSuggestion("todo D"))
      ))
    }


  }
}