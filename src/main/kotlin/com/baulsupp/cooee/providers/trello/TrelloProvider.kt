package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.queryList
import kotlinx.coroutines.coroutineScope

data class Boards(val list: List<BoardResponse>)
data class Cards(val list: List<Card>)

class TrelloProvider : BaseProvider() {
  override val name = "trello"

  override fun associatedServices(): Set<String> = setOf("trello")

  lateinit var boards: List<BoardResponse>

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    super.init(appServices, user)

    coroutineScope {
      boards = readBoards()
    }
  }

  suspend fun readBoards(): List<BoardResponse> = appServices.cache.get(user?.email, name, "boards") {
    Boards(client.queryList("https://api.trello.com/1/members/me/boards?filter=open&fields=id,name,url", userToken))
  }.list

  suspend fun readCards(boardId: String): List<Card> =
    appServices.cache.get(user?.email, name, boardId) {
      Cards(client.queryList("https://api.trello.com/1/boards/$boardId/cards?filter=open&fields=id,name,idBoard,shortUrl", userToken))
    }.list

  override suspend fun matches(command: String): Boolean {
    return (command == name) || boards.any { it.url.endsWith("/$command") }
  }

  override suspend fun go(command: String, vararg args: String): GoResult {
    if (command == name) {
      if (args.contentEquals(arrayOf("boards"))) {
        return Completed("Boards: " + boards.joinToString(", ") { it.url.split("/").last() })
      }

      return RedirectResult("https://trello.com/")
    } else {
      boards.forEach {
        if (it.url.endsWith("/$command")) {
          return RedirectResult(it.url)
        }
      }
    }

    return Unmatched
  }

  override suspend fun suggest(command: String): List<Suggestion> {
    return TrelloSuggester(this, check("caseinsensitive")).suggest(command)
  }
}
