package com.baulsupp.cooee.suggester

data class Suggestion(
  val line: String,
  val provider: String? = null,
  val description: String? = null,
  val type: SuggestionType? = null,
  val list: List<Suggestion>? = null,
  val url: String? = null,
  val message: String? = null
) {
  fun startsWith(command: String): Boolean {
    return line.startsWith(command)
  }

  fun contains(command: String): Boolean {
    return line.contains(command)
  }
}
