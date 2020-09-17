package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.CommandSuggestion
import com.baulsupp.cooee.p.TodoRequest
import com.baulsupp.cooee.p.TodoResponse
import com.baulsupp.cooee.services.CombinedProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class TodoController(val combinedProvider: CombinedProvider) {
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