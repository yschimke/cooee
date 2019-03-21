package com.baulsupp.cooee.providers

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.suggester.Suggestion

interface ProviderFunctions {
  suspend fun go(command: String, vararg args: String): GoResult

  suspend fun matches(command: String): Boolean

  suspend fun suggest(command: String): List<Suggestion>
  suspend fun todo(): List<Suggestion>
}
