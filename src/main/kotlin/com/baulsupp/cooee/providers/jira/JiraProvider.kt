package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.Provider
import com.baulsupp.cooee.services.jira.model.Project
import com.baulsupp.okurl.kotlin.queryList
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient

class JiraProvider(val url: String, val client: OkHttpClient) : Provider {
  override suspend fun url(command: String, args: List<String>): GoResult {
    val link = if (command == "jira") args.firstOrNull() else command

    return RedirectResult("${url}browse/$link")
  }

  val projects: List<Project> by lazy {
    runBlocking {
      client.queryList<Project>("${url}rest/api/2/project")
    }
  }

  // TODO
  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      // TODO relevance based issues
      val potential = projects.map { it.key } + listOf("TRANS-1234", "TRANS-123", "TRANS-1235")
      return potential.filter { it.startsWith(command) }
    }

    override suspend fun matches(command: String): Boolean {
      return (command == "jira" || command.matches("[A-Z]+(?:-\\d+)?".toRegex())) && projects.any {
        command == it.key || command.startsWith(
          "${it.key}-"
        )
      }
    }
  }
}
