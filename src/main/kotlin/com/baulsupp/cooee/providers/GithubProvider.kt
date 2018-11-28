package com.baulsupp.cooee.providers

object GithubProvider : Provider {
  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun url(command: String, args: List<String>): RedirectResult {
    val link = if (command == "gh") args.firstOrNull() else command

    if (link != null) {
      val r = "(\\w+)/(\\w+)(?:#(\\d+))?".toRegex()

      val result = r.matchEntire(link)

      if (result != null) {
        val (org, project, link) = result.destructured

        if (link.isEmpty()) {
          return RedirectResult("https://github.com/$org/$project")
        } else {
          return RedirectResult("https://github.com/$org/$project/issues/$link")
        }
      }
    }

    return RedirectResult.UNMATCHED
  }

  override suspend fun matches(command: String): Boolean {
    return command == "gh" || command.matches("\\w+/\\w+(?:#\\d+)?".toRegex())
  }
}
