package com.baulsupp.cooee.completion

class SimpleCommandCompleter(private val commands: List<String>) : CommandCompleter {
  constructor(vararg commands: String) : this(listOf(*commands))

  override suspend fun matches(command: String): Boolean = commands.contains(command)

  override suspend fun suggestCommands(command: String): List<Completion> =
    commands.filter { it.startsWith(command) }.map { Completion(it) }
}
