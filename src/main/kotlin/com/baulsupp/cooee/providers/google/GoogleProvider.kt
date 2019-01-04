package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.okurl.kotlin.query

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

  override fun argumentCompleter(): ArgumentCompleter {
    return object: ArgumentCompleter {
      override suspend fun suggestArguments(command: String, arguments: List<String>?): List<String>? {
        if (arguments == null) {
          return listOf()
        }

        var query = "http://suggestqueries.google.com/complete/search?client=firefox&q=" + arguments.joinToString("+")
        val result = appServices.client.query<List<Any>>(query)

        return (result[1] as? List<*>)?.map(Any?::toString)
      }
    }
  }
}
