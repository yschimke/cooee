package com.baulsupp.cooee.providers.bookmarks

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.BaseProvider

class BookmarksProvider : BaseProvider() {
  override val name = "bookmarks"

  val bookmarks: Map<String, String>
    get() = configuredBookmarks() ?: defaultBookmarks

  private fun configuredBookmarks() = instance?.config?.get("bookmarks") as? Map<String, String>?

  override suspend fun url(command: String, args: List<String>): GoResult {
    return if (command == "bookmarks") {
      bookmarksCommand(args)
    } else {
      val url = bookmarks[command]

      when (url) {
        null -> Unmatched
        else -> RedirectResult(url)
      }
    }
  }

  private fun bookmarksCommand(args: List<String>): GoResult {
    if (db != null) {
      if (args.firstOrNull() == "add") {
        // TODO error checking
        val newBookmarks = configuredBookmarks().orEmpty() + (args[1] to args[2])
//        instance?.config?.plus("bookmarks")
      } else if (args.firstOrNull() == "remove") {

      }
    }

    return Unmatched
  }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      return knownCommands().filter { it.startsWith(command) }
    }

    override suspend fun matches(command: String): Boolean {
      return knownCommands().contains(command)
    }
  }

  private fun knownCommands() = bookmarks.keys + "bookmarks"

  companion object {
    val defaultBookmarks =
      mutableMapOf(
        "google" to "https://google.com",
        "facebook" to "https://facebook.com",
        "twitter" to "https://m.twitter.com",
        "gmail" to "https://mai.google.com"
      )
  }
}
