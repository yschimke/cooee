package com.baulsupp.cooee.providers.bookmarks

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider

class BookmarksProvider : BaseProvider() {
  override val name = "bookmarks"

  private val bookmarks: Map<String, String>
    get() = configuredBookmarks() ?: defaultBookmarks

  private fun configuredBookmarks() = instance?.config?.get("bookmarks") as? Map<String, String>?

  override suspend fun go(command: String, args: List<String>): GoResult {
    return if (command == "bookmarks") {
      bookmarksCommand(args)
    } else {
      val url = buildTargetUrl(command, args)

      if (url != null) RedirectResult(url) else Unmatched
    }
  }

  private fun buildTargetUrl(command: String, args: List<String> = listOf()): String? {
    val pattern = bookmarks[command]

    return if (pattern != null && args.isNotEmpty())
      pattern.replace("%s", args.joinToString("+"))
    else
      pattern
  }

  private suspend fun bookmarksCommand(args: List<String>): GoResult {
    if (instance != null) {
      val previousBookmarks = configuredBookmarks().orEmpty()
      val newBookmarks = when {
        args.firstOrNull() == "add" -> // TODO error checking
          previousBookmarks + (args[1] to args[2])
        args.firstOrNull() == "remove" -> previousBookmarks - args[1]
        else -> return Unmatched
      }

      appServices.providerStore.store(instance!!.copy(config = instance!!.config.plus("bookmarks" to newBookmarks)))
      return Completed("bookmarks updated")
    }

    return Unmatched
  }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      return knownCommands().filter { it.startsWith(command) }
    }

    override suspend fun matches(command: String): Boolean {
      return command == "bookmarks" || knownCommands().contains(command)
    }
  }

  override fun argumentCompleter(): ArgumentCompleter {
    val userBookmarks = configuredBookmarks()

    val suggestions = if (userBookmarks != null) {
      userBookmarks.map { "remove ${it.key}" } + listOf("add", "remove")
    } else {
      null
    }

    return SimpleArgumentCompleter(suggestions)
  }

  private fun knownCommands() = bookmarks.keys + "bookmarks"

  companion object {
    val defaultBookmarks =
      mutableMapOf(
        "google" to "https://google.com",
        "facebook" to "https://facebook.com",
        "twitter" to "https://m.twitter.com",
        "gmail" to "https://mail.google.com"
      )
  }
}
