package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType

val TrelloHomepage = Suggestion(
  "trello",
  provider = "trello",
  description = "Trello Homepage",
  url = "https://trello.com",
  type = SuggestionType.LINK
)

class TrelloSuggester(val provider: TrelloProvider) : Suggester {
  override suspend fun suggest(command: String): List<Suggestion> {
    val boards = provider.boards.map { boardCompletion(it) }

    return (boards + TrelloHomepage).filter { it.startsWith(command) }
  }

  private fun boardCompletion(it: BoardResponse) =
    Suggestion(
      it.url.split("/").last(),
      provider = provider.name, description = "Trello Board: ${it.name}", url = it.url, type = SuggestionType.LINK
    )
}
