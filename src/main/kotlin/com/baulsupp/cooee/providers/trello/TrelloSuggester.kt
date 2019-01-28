package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion

val TrelloHomepage = Suggestion("trello", description = "Trello Homepage")

class TrelloSuggester(val provider: TrelloProvider) : Suggester {
  override suspend fun suggest(command: String): List<Suggestion> {
    val boards = provider.boards.map { boardCompletion(it) }

    return (boards + TrelloHomepage).filter { it.contains(command) }
  }

  private fun boardCompletion(it: BoardResponse) =
    Suggestion(it.url.split("/").last(), description = "Trello Board: ${it.name}")
}
