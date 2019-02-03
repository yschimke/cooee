package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType

class JiraCommandCompleter(val provider: JiraProvider) : Suggester {
  override suspend fun suggest(command: String): List<Suggestion> {
    val parts = command.split("\\s+".toPattern())

    if (parts.size > 2) {
      return listOf()
    } else if (parts.size == 2) {
      if (parts[0].isIssueOrPartialIssue()) {
        return listOf(
          Suggestion(
            command.completeLastWord("vote"),
            provider = provider.name,
            description = "Vote for ${parts[0]}",
            type = SuggestionType.COMMAND
          ),
          Suggestion(
            command.completeLastWord("comment"),
            provider = provider.name,
            description = "Comment on ${parts[0]}",
            type = SuggestionType.COMMAND
          )
        )
      }
    }

    return when {
      command == "" -> listOf()
      command.isProjectOrPartialProject() -> {
        provider.projects.filter { it.projectKey.startsWith(command) }.flatMap {
          mostLikelyProjectIssues(it) + listOfNotNull(projectCompletion(it))
        }
      }
      command.isProjectIssueStart() -> {
        val projectCode = command.projectCode()!!
        provider.issues(projectCode)?.issues?.map { issueToCompletion(it) }.orEmpty()
      }
      command.isIssueOrPartialIssue() -> mostLikelyIssueCompletions(command)
      else -> listOf()
    }
  }

  private fun projectCompletion(project: ProjectReference): Suggestion? =
    Suggestion(
      project.projectKey,
      provider = provider.name,
      description = "JIRA: " + project.project.name,
      type = SuggestionType.LINK,
      url = project.url
    )

  private suspend fun mostLikelyProjectIssues(project: ProjectReference): List<Suggestion> =
    provider.projectIssues(project).issues.map { issueToCompletion(it) }

  private fun issueToCompletion(it: Issue) =
    Suggestion(
      it.key,
      provider = provider.name,
      description = it.fields["summary"].toString(),
      url = it.fields["url"].toString(),
      type = SuggestionType.LINK
    )

  private suspend fun mostLikelyIssueCompletions(issueKey: String): List<Suggestion> {
    val project = provider.projects.find { it.projectKey == issueKey.projectCode() } ?: return listOf()
    val issue = provider.issue(project, issueKey)
    val issueCompletion = issueToCompletion(issue)

    return listOf(issueCompletion)
  }
}

private fun String.completeLastWord(s: String): String {
  val parts = split(" +".toPattern())
  return substring(0, length - parts.last().length) + s
}
