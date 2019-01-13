package com.baulsupp.cooee.completion

interface ArgumentCompleter {
  suspend fun suggestArguments(command: String, arguments: List<String>? = null): List<Completion>
}
