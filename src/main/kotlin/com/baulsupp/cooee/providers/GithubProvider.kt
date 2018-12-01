package com.baulsupp.cooee.providers

object GithubProvider : Provider {
  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun url(command: String, args: List<String>): RedirectResult {
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

    return RedirectResult.UNMATCHED
  }

  override suspend fun matches(command: String): Boolean {
    return command == "gh" || command.matches("\\w+/\\w+(?:#\\d+)?".toRegex())
  }
}
