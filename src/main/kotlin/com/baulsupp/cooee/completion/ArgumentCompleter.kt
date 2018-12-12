package com.baulsupp.cooee.completion

interface ArgumentCompleter {
  suspend fun suggestArguments(command: String): List<String>?
}
