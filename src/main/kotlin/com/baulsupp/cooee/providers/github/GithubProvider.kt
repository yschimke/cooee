package com.baulsupp.cooee.providers.github

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.Provider

object GithubProvider : Provider {
  override suspend fun url(command: String, args: List<String>): GoResult {
    if (command != null) {
      val r = "(\\w+)/(\\w+)(?:#(\\d+))?".toRegex()

      val result = r.matchEntire(command)

      if (result != null) {
        val (org, project, id) = result.destructured

        return if (id.isEmpty()) {
          RedirectResult("https://github.com/$org/$project")
        } else {
          RedirectResult("https://github.com/$org/$project/issues/$id")
        }
      }
    }

    return Unmatched
  }

  // TODO
  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      return listOf("yschimke/cooee", "yschimke/cooee-cli", "square/okhttp", "tgmcclen/cooee-web")
    }

    override suspend fun matches(command: String): Boolean {
      return command.matches("\\w+/\\w+(?:#\\d+)?".toRegex())
    }
  }
}
