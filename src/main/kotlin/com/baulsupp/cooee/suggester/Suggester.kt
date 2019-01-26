package com.baulsupp.cooee.suggester

interface Suggester {
  suspend fun suggest(command: String): List<Suggestion>
}
