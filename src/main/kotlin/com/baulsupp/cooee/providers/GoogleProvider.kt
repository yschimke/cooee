package com.baulsupp.cooee.providers

class GoogleProvider : Provider {
  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun url(command: String, args: List<String>): RedirectResult =
    RedirectResult("https://www.google.com/search?q=${args.joinToString(" ")}")

  override suspend fun matches(command: String): Boolean = command == "g" || command == "google"
}
