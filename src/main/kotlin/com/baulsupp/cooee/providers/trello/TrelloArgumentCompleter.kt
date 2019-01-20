package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.Completion
import com.baulsupp.cooee.completion.Completion.Companion.completions

class TrelloArgumentCompleter(val provider: TrelloProvider) : ArgumentCompleter {
  override suspend fun suggestArguments(command: String, arguments: List<String>?): List<Completion> {
    return completions("boards")
  }
}
