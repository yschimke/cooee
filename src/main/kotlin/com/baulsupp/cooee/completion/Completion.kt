package com.baulsupp.cooee.completion

data class Completion(val completion: String, val provider: String? = null, val description: String? = null) {
  fun startsWith(command: String): Boolean {
    return completion.startsWith(command)
  }

  companion object {
    fun completions(vararg strings: String) = strings.map { Completion(it) }
  }
}
