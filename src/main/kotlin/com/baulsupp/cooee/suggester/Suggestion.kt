package com.baulsupp.cooee.suggester

data class Suggestion(
  val line: String,
  val provider: String,
  val description: String,
  val type: SuggestionType,
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

data class SuggestionList(val suggestions: List<Suggestion>)
