package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.suggester.Suggestion

class JiraCommandCompleter(val provider: JiraProvider) : CommandCompleter {
  override suspend fun suggestCommands(command: String): List<Suggestion> {
    val visibleProjects = provider.projects

    val projectKeys = visibleProjects.map { it.projectKey }

    return when {
      command == "" -> listOf()
      command.isProjectOrPartialProject() -> projectKeys.filter { it.startsWith(command) }.flatMap {
        provider.mostLikelyProjectIssues(it) + listOfNotNull(provider.projectCompletion(it))
      }
      command.isProjectIssueStart() -> provider.mostLikelyProjectIssues(command.projectCode()!!)
      command.isIssueOrPartialIssue() -> provider.mostLikelyIssueCompletions(command)
      else -> listOf()
    }
  }

  override suspend fun matches(command: String): Boolean {
    return (command.isProjectOrIssue()) && provider.projects.any {
      command == it.projectKey || command.startsWith("${it.projectKey}-")
    }
  }
}
