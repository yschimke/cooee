package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

class JiraCommandCompleter(val provider: JiraProvider) : Suggester {
  val caseInsensitive = provider.check("caseinsensitive")

  override suspend fun suggest(command: String): List<Suggestion> {
    val commandFixed = command.fixCase()

    val parts = commandFixed.split("\\s+".toPattern())

    if (parts.size > 2) {
      return listOf()
    } else if (parts.size == 2) {
      if (parts[0].isIssueOrPartialIssue()) {
        return listOf(
          Suggestion(
            commandFixed.completeLastWord("vote"),
            provider = provider.name,
            description = "Vote for ${parts[0]}",
            type = SuggestionType.COMMAND
          ),
          Suggestion(
            commandFixed.completeLastWord("comment"),
            provider = provider.name,
            description = "Comment on ${parts[0]}",
            type = SuggestionType.COMMAND
          )
        )
      }
    }

    return when {
      commandFixed == "" -> listOf()
      commandFixed.isProjectOrPartialProject() -> {
        provider.projects.filter { it.projectKey.startsWith(commandFixed) }.flatMap {
          mostLikelyProjectIssues(it) + listOfNotNull(projectCompletion(it))
        }
      }
      commandFixed.isProjectIssueStart() -> {
        val projectCode = commandFixed.projectCode()!!
        provider.issues(projectCode)?.issues?.map { issueToCompletion(it) }.orEmpty()
      }
      commandFixed.isIssueOrPartialIssue() -> mostLikelyIssueCompletions(commandFixed)
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

  private fun String.fixCase(): String {
    if (!caseInsensitive) {
      return this
    }

    val parts = split("\\s+".toPattern(), limit = 2)

    val inputCommandFixed = parts.first().toUpperCaseAsciiOnly()

    if (inputCommandFixed == parts.first()) {
      return this
    }

    return inputCommandFixed + if (parts.size == 2) " " + parts.last() else ""
  }

  private fun String.completeLastWord(s: String): String {
    val parts = split("\\s+".toPattern())
    return substring(0, length - parts.last().length) + s
  }
}

