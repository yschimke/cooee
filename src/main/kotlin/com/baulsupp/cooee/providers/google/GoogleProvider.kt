package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.providers.BaseProvider

class GoogleProvider : BaseProvider() {
  override val name = "google"

  override suspend fun go(command: String, args: List<String>): GoResult = if (args.isEmpty()) {
    RedirectResult("https://www.google.com/")
  } else {
    RedirectResult("https://www.google.com/search?q=${args.joinToString("+")}${if (command == "gl") "&btnI" else ""}")
  }

  override fun commandCompleter(): CommandCompleter {
    return SimpleCommandCompleter("g", "gl")
  }
}
