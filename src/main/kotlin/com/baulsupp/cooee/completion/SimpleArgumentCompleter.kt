package com.baulsupp.cooee.completion


class SimpleArgumentCompleter(val arguments: List<String>?): ArgumentCompleter {
  override suspend fun suggestArguments(command: String): List<String>? = arguments
}
