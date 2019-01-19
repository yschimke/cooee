package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.Completion
import com.baulsupp.okurl.services.trello.model.BoardResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi

val TrelloHomepage = Completion("trello", description = "Trello Homepage")

@ExperimentalCoroutinesApi
class TrelloCommandCompleter(val provider: TrelloProvider) : CommandCompleter {
  override suspend fun suggestCommands(command: String): List<Completion> {
    val boards = provider.boards.map { boardCompletion(it) }

    return (boards + TrelloHomepage).filter { it.contains(command) }
  }

  private fun boardCompletion(it: BoardResponse) =
    Completion(it.url.split("/").last(), "Trello: ${it.name}")

  override suspend fun matches(command: String): Boolean {
    return command == TrelloHomepage.completion || provider.boards.any {
      it.url.split("/").last() == command
    }
  }
}
