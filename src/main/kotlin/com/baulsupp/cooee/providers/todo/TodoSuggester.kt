package com.baulsupp.cooee.providers.todo

import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion

class TodoSuggester(val todoProvider: TodoProvider) : Suggester {
  override suspend fun suggest(command: String): List<Suggestion> = when {
    "todo".startsWith(command) -> todoProvider.todo() + todoProvider.todoCommand
    command.startsWith("todo.") -> todoProvider.todo().filter { it.line.startsWith(command) }
    else -> listOf()
  }
}
