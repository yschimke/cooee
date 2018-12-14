package com.baulsupp.cooee.providers.bookmarks

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.Provider

class BookmarksProvider : Provider {
  val bookmarks =
    mutableMapOf("man" to "https://man.com", "facebook" to "https://facebook.com", "twitter" to "https://m.twitter.com")

  override suspend fun url(command: String, args: List<String>): GoResult {
    val url = bookmarks[command]

    return when (url) {
      null -> Unmatched
      else -> RedirectResult(url)
    }
  }

  // TODO
  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      return bookmarks.keys.filter { it.startsWith(command) }
    }

    override suspend fun matches(command: String): Boolean {
      return bookmarks.keys.contains(command)
    }
  }
}
