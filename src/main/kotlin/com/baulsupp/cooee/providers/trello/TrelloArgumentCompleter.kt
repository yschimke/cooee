package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.Suggestion.Companion.completions

class TrelloArgumentCompleter(val provider: TrelloProvider) : ArgumentCompleter {
  override suspend fun suggestArguments(command: String, arguments: List<String>?): List<String> {
    return listOf("boards")
  }
}
