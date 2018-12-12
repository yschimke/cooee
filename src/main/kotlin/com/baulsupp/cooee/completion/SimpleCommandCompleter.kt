package com.baulsupp.cooee.completion

class SimpleCommandCompleter(val commands: List<String>): CommandCompleter {
  constructor(vararg commands: String) : this(listOf(*commands))

  override suspend fun matches(command: String): Boolean = commands.contains(command)

  override suspend fun suggestCommands(command: String): List<String> = commands.filter { it.startsWith(command) }
}
