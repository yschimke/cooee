package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType

class TwitterSuggester(val provider: TwitterProvider) : Suggester {
  override suspend fun suggest(command: String): List<Suggestion> {
    return when {
      command.length < 2 || !command.startsWith("@") -> listOf()
      else -> try {
        val screenName = command.substring(1)
        provider.friends.filter { it.screen_name.startsWith(screenName, ignoreCase = true) }
      } catch (e: Exception) {
        provider.log.warn("Failed to suggest completions", e)
        listOf<Friend>()
      }.map { Suggestion("@${it.screen_name}", type = SuggestionType.COMMAND, description = "DM ${it.name}") }
    }
  }
}
