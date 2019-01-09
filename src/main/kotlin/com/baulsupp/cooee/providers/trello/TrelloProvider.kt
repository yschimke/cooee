package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.services.trello.model.BoardResponse

class TrelloProvider : BaseProvider() {
  override val name = "trello"

  override fun associatedServices(): Set<String> = setOf("trello")

  lateinit var boards: List<BoardResponse>

  suspend fun userBoards(): List<BoardResponse> {
    if (!this::boards.isInitialized) {
      boards = client.queryList("https://api.trello.com/1/members/me/boards", userToken)
    }

    return boards
  }

  override suspend fun go(command: String, vararg args: String): GoResult {
    if (command == name) {
      if (args.contentEquals(arrayOf("boards"))) {
        return Completed("Boards: " + userBoards().joinToString(", ") { it.url.split("/").last() })
      }

      return RedirectResult("https://trello.com/")
    } else {
      userBoards().forEach {
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
