package com.baulsupp.cooee.providers

import com.baulsupp.cooee.services.jira.model.Project
import com.baulsupp.okurl.kotlin.queryList
import okhttp3.OkHttpClient

class JiraProvider(val url: String, val client: OkHttpClient) : Provider {
  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun url(command: String, args: List<String>): RedirectResult {
    val link = if (command == "jira") args.firstOrNull() else command

    return RedirectResult("${url}browse/${command}")
  }

  override suspend fun matches(command: String): Boolean {
    return (command == "jira" || command.matches("[A-Z]+(?:-\\d+)?".toRegex())) && projects().any {
      command == it.key || command.startsWith(
        "${it.key}-"
      )
    }
  }

  suspend fun projects(): List<Project> = client.queryList("${url}rest/api/2/project")
}
