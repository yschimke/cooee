package com.baulsupp.cooee.completion

import com.baulsupp.cooee.suggester.Suggestion

class SimpleCommandCompleter(private val commands: List<String>) : CommandCompleter {
  constructor(vararg commands: String) : this(listOf(*commands))

  override suspend fun matches(command: String): Boolean = commands.contains(command)

  override suspend fun suggestCommands(command: String): List<Suggestion> =
    commands.filter { it.startsWith(command) }.map { Suggestion(it) }
}
