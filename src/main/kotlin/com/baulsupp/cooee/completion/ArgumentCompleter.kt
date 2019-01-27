package com.baulsupp.cooee.completion

@Deprecated("use Suggester")
interface ArgumentCompleter {
  suspend fun suggestArguments(command: String, arguments: List<String>? = null): List<String>
}
