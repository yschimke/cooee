package com.baulsupp.cooee.completion

import com.baulsupp.cooee.suggester.Suggestion

@Deprecated("use Suggester")
interface CommandCompleter {
  suspend fun suggestCommands(command: String): List<Suggestion>

  suspend fun matches(command: String): Boolean
}
