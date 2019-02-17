package com.baulsupp.cooee.providers

import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType

class BaseSuggester(val name: String, val commandCompleter: CommandCompleter, val argumentCompleter: ArgumentCompleter) : Suggester {
  override suspend fun suggest(command: String): List<Suggestion> {
    val parts = command.split("\\s+".toPattern())

    return when {
      parts.size == 1 -> suggestCommands(command)
      commandCompleter.matches(parts.first()) -> suggestArguments(command, parts)
      else -> listOf()
    }
  }

  private suspend fun suggestCommands(command: String) =
    commandCompleter.suggestCommands(command)

  private suspend fun suggestArguments(command: String, parts: List<String>): List<Suggestion> {
    val suggestions = argumentCompleter.suggestArguments(parts.first(), parts.drop(1))
    return suggestions.map {
      val line = command.substring(0, command.length - parts.last().length) + it
      Suggestion(line, provider = name, description = "Command for '$line'", type = SuggestionType.UNKNOWN)
    }
  }
}
