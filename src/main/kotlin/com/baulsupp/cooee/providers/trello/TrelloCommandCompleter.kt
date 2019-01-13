package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.Completion

class TrelloCommandCompleter(val provider: TrelloProvider) : CommandCompleter {
  override suspend fun suggestCommands(command: String): List<Completion> {
    val site = listOf("trello")
    val boards = provider.userBoards().map { it.url.split("/").last() }

    return (site + boards).filter { it.startsWith(command) }.map { Completion(it) }
  }

  override suspend fun matches(command: String): Boolean {
    return command == "trello" || provider.userBoards().any {
      it.url.split("/").last() == command
    }
  }
}
