package com.baulsupp.cooee.completion

import com.baulsupp.cooee.suggester.Suggestion

interface CommandCompleter {
  suspend fun suggestCommands(command: String): List<Suggestion>

  suspend fun matches(command: String): Boolean
}
