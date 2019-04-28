package com.baulsupp.cooee.providers.circleci

import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion

class CircleCiSuggester(val provider: CircleCiProvider): Suggester {
  override suspend fun suggest(command: String): List<Suggestion> = when {
    "circleci".startsWith(command) -> listOf(provider.circleciCommand)
    else -> listOf()
  }
}
