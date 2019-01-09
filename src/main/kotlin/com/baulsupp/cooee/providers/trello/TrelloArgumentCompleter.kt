package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.completion.ArgumentCompleter

class TrelloArgumentCompleter(val provider: TrelloProvider) : ArgumentCompleter {
  override suspend fun suggestArguments(command: String, arguments: List<String>?): List<String> {
    return listOf("boards")
  }
}
