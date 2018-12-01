package com.baulsupp.cooee.providers.github

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.Provider
import com.baulsupp.cooee.providers.Target

object GithubProvider : Provider {
  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun url(command: String, args: List<String>): GoResult {
    val link = if (command == "gh") args.firstOrNull() else command

    if (link != null) {
      val r = "(\\w+)/(\\w+)(?:#(\\d+))?".toRegex()

      val result = r.matchEntire(link)

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

  override suspend fun matches(command: String): Boolean {
    return command == "gh" || command.matches("\\w+/\\w+(?:#\\d+)?".toRegex())
  }
}
