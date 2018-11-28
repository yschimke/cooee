package com.baulsupp.cooee.providers

interface Provider {
  suspend fun url(command: String, args: List<String>): RedirectResult

  suspend fun targets(command: String, args: List<String>): List<Target>

  suspend fun matches(command: String): Boolean
}
