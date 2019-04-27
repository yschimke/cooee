package com.baulsupp.cooee.providers.todo

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType

class TodoProvider : BaseProvider() {
  override val name = "todo"

  override suspend fun go(command: String, vararg args: String): GoResult =
    when {
      command == "todo" -> Completed("todo added")
      else -> Unmatched
    }

  override suspend fun todo(): List<Suggestion> {
    return listOf(
      Suggestion("todo.a", name, "First Todo Desc", message = "First Todo Message", type = SuggestionType.INFO),
      Suggestion("todo.c", name, "Third Todo Desc", url = "https://nba.com", type = SuggestionType.LINK)
    )
  }
}
