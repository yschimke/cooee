package com.baulsupp.cooee.completion

import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType

class SimpleCommandCompleter(val name: String, private val commands: List<String>) : CommandCompleter {
  override suspend fun matches(command: String): Boolean = commands.contains(command)

  override suspend fun suggestCommands(command: String): List<Suggestion> =
    commands.filter { it.startsWith(command) }.map {
      Suggestion(
        it,
        provider = name,
        description = "Command for '$it'",
        type = SuggestionType.UNKNOWN
      )
    }
}
