package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.services.jira.model.Project
import com.baulsupp.okurl.kotlin.queryList
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient

class JiraProvider(val url: String, val client: OkHttpClient) : BaseProvider() {
  override val name = "jira"

  override suspend fun url(command: String, args: List<String>): GoResult = if (command.isProjectOrIssue()) {
    RedirectResult("${url}browse/$command")
  } else {
    Unmatched
  }

  val projects: List<Project> by lazy {
    runBlocking {
      client.queryList<Project>("${url}rest/api/2/project")
    }
  }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      val projectKeys = projects.map { it.key }

      return when {
        command == "" -> listOf()
        command.isProjectOrPartialProject() -> projectKeys.filter { it.startsWith(command) }.flatMap {
          listOf(
            it,
            "$it-"
          )
        }
        command.isProjectIssueStart() -> mostLikelyProjectIssues(command.projectCode()!!)
        command.isIssueOrPartialIssue() -> mostLikelyIssueCompletions(command)
        else -> listOf("X") // consider returning all projects
      }
    }

    override suspend fun matches(command: String): Boolean {
      return (command.isProjectOrIssue()) && projects.any {
        command == it.key || command.startsWith(
          "${it.key}-"
        )
      }
    }
  }

  private fun mostLikelyProjectIssues(project: String): List<String> =
    listOf("$project-123", "$project-1234", "$project-1235")

  private fun mostLikelyIssueCompletions(issue: String): List<String> =
    when {
      issue.issueNumber()!!.length > 4 -> listOf(issue)
      else -> listOf(issue) + (0..9).map { issue + it }
    }

  // TODO introduce sealed type for JIRA issues to avoid double parsing and null hacks
  private fun String.isProjectOrPartialIssue() = matches("[A-Z]+(?:-\\d*)?".toRegex())

  private fun String.isProjectOrIssue() = matches("[A-Z]+(?:-\\d+)?".toRegex())
  private fun String.isProjectOrPartialProject() = matches("[A-Z]+".toRegex())
  private fun String.isProjectIssueStart() = matches("[A-Z]+-".toRegex())
  private fun String.isIssueOrPartialIssue() = matches("[A-Z]+-\\d+".toRegex())
  private fun String.projectCode(): String? =
    if (isProjectOrPartialIssue()) split('-')[0] else throw NullPointerException(
      "null for $this"
    )

  private fun String.issueNumber(): String? =
    if (isIssueOrPartialIssue()) split('-')[1] else throw NullPointerException(
      "null for $this"
    )
}
