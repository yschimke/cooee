package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.Completion

class JiraCommandCompleter(val provider: JiraProvider) : CommandCompleter {
  override suspend fun suggestCommands(command: String): List<Completion> {
    val visibleProjects = provider.allprojects()

    val projectKeys = visibleProjects.map { it.projectKey }

    return when {
      command == "" -> listOf()
      command.isProjectOrPartialProject() -> projectKeys.filter { it.startsWith(command) }.flatMap {
        listOf(it, "$it-")
      }
      command.isProjectIssueStart() -> provider.mostLikelyProjectIssues(command.projectCode()!!)
      command.isIssueOrPartialIssue() -> provider.mostLikelyIssueCompletions(command)
      else -> listOf()
    }.map { Completion(it) }
  }

  override suspend fun matches(command: String): Boolean {
    return (command.isProjectOrIssue()) && provider.allprojects().any {
      command == it.projectKey || command.startsWith("${it.projectKey}-")
    }
  }
}
