package com.baulsupp.cooee.services.github

import com.apollographql.apollo.ApolloClient
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandStatus
import com.baulsupp.cooee.p.CommandSuggestion
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.p.command
import com.baulsupp.cooee.p.done
import com.baulsupp.cooee.p.redirect
import com.baulsupp.cooee.p.single_command
import com.baulsupp.cooee.p.unmatched
import com.baulsupp.cooee.services.Provider
import com.baulsupp.okurl.services.github.GithubAuthInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.temporal.ChronoUnit

class GithubProvider(val apolloClient: ApolloClient) : Provider("github",
    GithubAuthInterceptor().serviceDefinition) {
  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    val arguments = request.parsed_command.drop(1)

    if (request.parsed_command.firstOrNull() == "github") {
      return githubCommand(arguments)
    }

    val command = request.single_command ?: return null

    val result = issueProjectRegex.matchEntire(command) ?: return null

    val (org, project, id) = result.destructured

    return when {
      id.isEmpty() -> {
        flowOf(projectResponse(org, project))
      }
      arguments.isEmpty() -> {
        flowOf(issueResponse(org, project, id.toInt()))
      }
      arguments.firstOrNull() == "comment" -> {
        flowOf(commentResponse(org, project, id.toInt(), arguments.drop(1).joinToString(" ")))
      }
      else -> {
        flowOf(CommandResponse.unmatched())
      }
    }
  }

  private suspend fun projectResponse(
    org: String,
    project: String
  ): CommandResponse {
    val projectDetails = project(org, project) ?: return CommandResponse.unmatched()

    return CommandResponse(url = projectDetails.url.toString(), status = CommandStatus.DONE,
        message = "${projectDetails.name}: ${projectDetails.description}")
  }

  private suspend fun issueResponse(
    org: String,
    project: String,
    id: Int
  ): CommandResponse {
    val issueDetails = issue(org, project, id) ?: return CommandResponse.unmatched()

    return CommandResponse(url = issueDetails.asIssue?.url?.toString() ?: issueDetails.asPullRequest?.url?.toString(),
        status = CommandStatus.DONE,
        message = "${issueDetails.asIssue?.title ?: issueDetails.asPullRequest?.title}")
  }

  private suspend fun commentResponse(
    org: String,
    project: String,
    id: Int,
    comment: String
  ): CommandResponse {
    val success = comment(org, project, id, comment)

    if (!success) {
      return CommandResponse.unmatched()
    }

    return CommandResponse.done("Commented")
  }

  suspend fun githubCommand(arguments: List<String>): Flow<CommandResponse> {
    return when {
      arguments.isEmpty() -> flowOf(githubWebsite)
      arguments == listOf("pulls") -> pullsCommand()
      else -> flowOf(CommandResponse.unmatched())
    }
  }

  suspend fun pullsCommand(): Flow<CommandResponse> {
    val from = Instant.now().minus(14, ChronoUnit.DAYS)

    return pulls().asFlow().filter {
      Instant.parse(it.updatedAt.toString()).isAfter(from)
    }.map { pr ->
      CommandResponse(status = CommandStatus.DONE, url = pr.permalink.toString(),
          message = pr.repository.nameWithOwner + "#" + pr.number + "\t" + pr.title
      )
    }
  }

  override suspend fun matches(command: String): Boolean {
    return command == "github" || matchesPattern(command)
  }

  private fun matchesPattern(command: String): Boolean {
    return command.matches(issueProjectRegex)
  }

  override suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> {
    return listOf(
        CompletionSuggestion.command(
            CommandSuggestion(provider = "github", description = "Github Website"), "github"),
    ) + projects().map {
      CompletionSuggestion.command(CommandSuggestion(provider = "github",
          description = it.description ?: "Github: ${it.nameWithOwner}", command = it.nameWithOwner,
          url = "https://github.com/${it.nameWithOwner}"),
          it.nameWithOwner)
    }
  }

  companion object {
    val githubWebsite = CommandResponse.redirect("https://github.com/")
    val issueProjectRegex = "([-a-zA-Z]+)/([-a-zA-Z]+)(?:#(\\d+))?".toRegex()
  }
}
