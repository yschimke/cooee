package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion

class JiraCommandCompleter(val provider: JiraProvider) : Suggester {
  override suspend fun suggest(command: String): List<Suggestion> {
    return when {
      command == "" -> listOf()
      command.isProjectOrPartialProject() -> {
        provider.projects.filter { it.projectKey.startsWith(command) }.flatMap {
          provider.mostLikelyProjectIssues(it) + listOfNotNull(provider.projectCompletion(command))
        }
      }
      command.isProjectIssueStart() -> {
        val projectCode = command.projectCode()!!
        provider.issues(projectCode)?.issues?.map { provider.issueToCompletion(it) }.orEmpty()
      }
      command.isIssueOrPartialIssue() -> provider.mostLikelyIssueCompletions(command)
      else -> listOf()
    }
  }
}
