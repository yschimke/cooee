package com.baulsupp.cooee.providers

import com.baulsupp.cooee.api.GoResult

interface Provider {
  suspend fun url(command: String, args: List<String>): GoResult

  suspend fun targets(command: String, args: List<String>): List<Target>

  suspend fun matches(command: String): Boolean
}
