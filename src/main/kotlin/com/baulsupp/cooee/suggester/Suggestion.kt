package com.baulsupp.cooee.suggester

data class Suggestion(
  val completion: String,
  val provider: String? = null,
  val description: String? = null,
  val type: SuggestionType? = SuggestionType.LINK,
  val list: List<Suggestion>? = null
) {
  fun startsWith(command: String): Boolean {
    return completion.startsWith(command)
  }

  fun contains(command: String): Boolean {
    return completion.contains(command)
  }

  companion object {
    fun completions(vararg strings: String) = strings.map { Suggestion(it) }
  }
}
