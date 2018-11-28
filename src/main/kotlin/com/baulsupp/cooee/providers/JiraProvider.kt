package com.baulsupp.cooee.providers

class JiraProvider(val url: String): Provider {
  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun url(command: String, args: List<String>): RedirectResult =
    RedirectResult("${url}browse/${command}")

  override suspend fun matches(command: String): Boolean = command == "TRANS" || command.startsWith("TRANS-")
}
