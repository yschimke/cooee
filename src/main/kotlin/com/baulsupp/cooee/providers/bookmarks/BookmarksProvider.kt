package com.baulsupp.cooee.providers.bookmarks

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType

class BookmarksProvider : BaseProvider() {
  override val name = "bookmarks"

  @Suppress("UNCHECKED_CAST")
  private val configuredBookmarks
    get() = config["bookmarks"] as? Map<String, String>? ?: mapOf()

  override suspend fun go(command: String, vararg args: String): GoResult {
    return if (command == "bookmarks") {
      bookmarksCommand(args.toList())
    } else {
      val url = buildTargetUrl(command, args.toList())

      if (url != null) RedirectResult(url) else Unmatched
    }
  }

  private fun buildTargetUrl(command: String, args: List<String> = listOf()): String? {
    val pattern = configuredBookmarks[command]

    return if (pattern != null && args.isNotEmpty())
      pattern.replace("%s", args.joinToString("+"))
    else
      pattern
  }

  private suspend fun bookmarksCommand(args: List<String>): GoResult {
    user?.let {
      val previousBookmarks = configuredBookmarks

      val newBookmarks = when {
        args.firstOrNull() == "add" -> // TODO error checking
          previousBookmarks + (args[1] to args[2])
        args.firstOrNull() == "remove" -> previousBookmarks - args[1]
        else -> return Unmatched
      }

      config["bookmarks"] = newBookmarks

      appServices.providerConfigStore.store(it.email, name, config)
      return Completed("bookmarks updated")
    }

    return Unmatched
  }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<Suggestion> {
      val bookmarks = configuredBookmarks.map { (k, v) -> Suggestion(k, name, type = SuggestionType.LINK, url = v) }
      return bookmarks + Suggestion("bookmarks", name, description = "Bookmark Options", type = SuggestionType.PREFIX)
    }

    override suspend fun matches(command: String): Boolean {
      return command == "bookmarks" || knownCommands().contains(command)
    }
  }

  override fun argumentCompleter(): ArgumentCompleter {
    val suggestions = if (user != null) {
      configuredBookmarks.map { "remove ${it.key}" } + listOf("add", "remove")
    } else {
      null
    }

    return SimpleArgumentCompleter(suggestions.orEmpty())
  }

  private fun knownCommands() = configuredBookmarks.keys + "bookmarks"

  companion object {
    fun loggedOut(): BaseProvider {
      return BookmarksProvider().apply { configure(mapOf("bookmarks" to defaultBookmarks)) }
    }

    val defaultBookmarks = mapOf(
      "google" to "https://google.com",
      "facebook" to "https://facebook.com",
      "twitter" to "https://m.twitter.com",
      "gmail" to "https://mail.google.com"
    )
  }
}
