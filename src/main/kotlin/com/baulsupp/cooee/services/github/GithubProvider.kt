package com.baulsupp.cooee.services.github

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.request.RequestHeaders
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandStatus
import com.baulsupp.cooee.p.CommandSuggestion
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.p.command
import com.baulsupp.cooee.p.redirect
import com.baulsupp.cooee.p.single_command
import com.baulsupp.cooee.p.unmatched
import com.baulsupp.cooee.services.Provider
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Token
import com.baulsupp.okurl.credentials.TokenValue
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
    if (request.parsed_command.firstOrNull() == "github") {
      return githubCommand(request.parsed_command.drop(1))
    }

    val r = "(\\w+)/([\\w-]+)(?:#(\\d+))?".toRegex()

    val command = request.single_command ?: return null

    val result = r.matchEntire(command) ?: return null

    val (org, project, id) = result.destructured

    return if (id.isEmpty()) {
      flowOf(CommandResponse.redirect("https://github.com/$org/$project"))
    } else {
      flowOf(CommandResponse.redirect("https://github.com/$org/$project/issues/$id"))
    }
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

  suspend fun <D : Operation.Data, T, V : Operation.Variables> graphqlQuery(
    meQuery: Query<D, T, V>
  ): Response<T> {
    val t = token()

    return apolloClient
        .query(meQuery)
        .run {
          val tokenString = ((t as? TokenValue)?.token as? Oauth2Token)?.accessToken
          if (tokenString != null) {
            toBuilder()
                .requestHeaders(RequestHeaders.Builder()
                    .addHeader("Authorization", "token $tokenString")
                    .build())
                .build()
          } else {
            this
          }
        }
        .await()
  }

  override suspend fun matches(command: String): Boolean {
    return command == "github" || matchesPattern(command) && projects().any {
      it.nameWithOwner == command
    }
  }

  private fun matchesPattern(command: String): Boolean {
    return command.matches("[a-zA-Z]+/[a-zA-Z]+(?:#\\d+)?".toRegex())
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
  }
}
