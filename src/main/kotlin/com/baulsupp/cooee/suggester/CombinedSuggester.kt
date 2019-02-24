package com.baulsupp.cooee.suggester

import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.providers.CombinedProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CombinedSuggester(val suggesters: List<BaseProvider>, val ignoreCase: Boolean) : Suggester {
  override suspend fun suggest(command: String): List<Suggestion> = coroutineScope {
    suggesters.map { provider ->
      async {
        try {
          val suggestions = provider.suggest(command)
          suggestions.filter { s -> s.startsWith(command, ignoreCase) }
            .map { it.copy(provider = provider.name) }
        } catch (e: Exception) {
          CombinedProvider.log.warn("suggestCommands failed: " + provider.name, e)
          listOf<Suggestion>()
        }
      }
    }.awaitAll().flatten()
  }
}
