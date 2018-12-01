package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.providers.Provider
import com.baulsupp.cooee.providers.Target

class GoogleProvider : Provider {
  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun url(command: String, args: List<String>): GoResult =
    RedirectResult("https://www.google.com/search?q=${args.joinToString(" ")}${if (command == "gl") "&btnI" else ""}")

  override suspend fun matches(command: String): Boolean = command == "g" || command == "gl" || command == "google"
}
