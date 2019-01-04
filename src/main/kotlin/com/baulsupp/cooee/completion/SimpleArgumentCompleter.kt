package com.baulsupp.cooee.completion

class SimpleArgumentCompleter(private val arguments: List<String>?) : ArgumentCompleter {
  override suspend fun suggestArguments(command: String, arguments: List<String>?)= arguments
}
