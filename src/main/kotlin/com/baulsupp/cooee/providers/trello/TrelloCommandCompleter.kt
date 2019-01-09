package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.completion.CommandCompleter

class TrelloCommandCompleter(val provider: TrelloProvider) : CommandCompleter {
  override suspend fun suggestCommands(command: String): List<String> {
    if ("trello".startsWith(command)) {
      // TODO fix for when board name is trello
      return listOf("trello")
    } else {
      return provider.userBoards().map { it.url.split("/").last() }
    }
  }

  override suspend fun matches(command: String): Boolean {
    return command == "trello" || provider.userBoards().any {
      println(it.url)
      it.url.split("/").last() == command
    }
  }
}
