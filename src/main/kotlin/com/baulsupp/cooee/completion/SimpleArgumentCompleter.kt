package com.baulsupp.cooee.completion

class SimpleArgumentCompleter(private val fixedArguments: List<String>) : ArgumentCompleter {
  constructor(vararg arguments: String) : this(listOf(*arguments))

  override suspend fun suggestArguments(command: String, arguments: List<String>?) =
    fixedArguments.map { Completion(it) }
}
