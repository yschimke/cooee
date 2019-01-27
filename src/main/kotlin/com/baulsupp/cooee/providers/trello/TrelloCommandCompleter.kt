package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.okurl.services.trello.model.BoardResponse

val TrelloHomepage = Suggestion("trello", description = "Trello Homepage")

class TrelloCommandCompleter(val provider: TrelloProvider) : CommandCompleter {
  override suspend fun suggestCommands(command: String): List<Suggestion> {
    val boards = provider.boards.map { boardCompletion(it) }

    return (boards + TrelloHomepage).filter { it.contains(command) }
  }

  private fun boardCompletion(it: BoardResponse) =
    Suggestion(it.url.split("/").last(), description = "Trello Board: ${it.name}")

  override suspend fun matches(command: String): Boolean {
    return command == TrelloHomepage.line || provider.boards.any {
      it.url.split("/").last() == command
    }
  }
}
