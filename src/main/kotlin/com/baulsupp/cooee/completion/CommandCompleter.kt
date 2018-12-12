package com.baulsupp.cooee.completion

interface CommandCompleter {
  suspend fun suggestCommands(command: String): List<String>

  suspend fun matches(command: String): Boolean
}
