package com.baulsupp.cooee.completion

interface CommandCompleter {
  suspend fun suggestCommands(command: String): List<Completion>

  suspend fun matches(command: String): Boolean
}
