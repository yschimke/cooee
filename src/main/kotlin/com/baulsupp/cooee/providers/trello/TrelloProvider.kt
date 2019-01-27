package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.services.trello.model.BoardResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class Boards(val list: List<BoardResponse>)
data class Cards(val list: List<Card>)

class TrelloProvider : BaseProvider() {
  override val name = "trello"

  override fun associatedServices(): Set<String> = setOf("trello")

  lateinit var boards: List<BoardResponse>
  lateinit var cards: Map<String, List<Card>>

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    super.init(appServices, user)

    coroutineScope {
      boards = readBoards()
      cards = boards.map {
        async {
          it.id to readCards(it.id)
        }
      }.awaitAll().toMap()
    }
  }

  private suspend fun readBoards(): List<BoardResponse> = appServices.cache.get(user?.email, name, "boards") {
    Boards(client.queryList("https://api.trello.com/1/members/me/boards", userToken))
  }.list

  private suspend fun readCards(boardId: String): List<Card> =
    appServices.cache.get<Cards>(user?.email, name, boardId) {
      Cards(client.queryList<Card>("https://api.trello.com/1/boards/$boardId/cards/open", userToken))
    }.list

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

  override fun commandCompleter(): CommandCompleter {
    return TrelloCommandCompleter(this)
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return TrelloArgumentCompleter(this)
  }
}
