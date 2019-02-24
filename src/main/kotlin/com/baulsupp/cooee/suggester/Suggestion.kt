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
  fun startsWith(command: String, ignoreCase: Boolean): Boolean {
    return line.startsWith(command, ignoreCase = ignoreCase)
  }

  fun contains(command: String, ignoreCase: Boolean): Boolean {
    return line.contains(command, ignoreCase = ignoreCase)
  }
}

data class SuggestionList(val suggestions: List<Suggestion>)
