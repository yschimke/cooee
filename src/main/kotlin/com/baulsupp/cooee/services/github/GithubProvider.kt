package com.baulsupp.cooee.services.github

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandSuggestion
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.p.command
import com.baulsupp.cooee.p.redirect
import com.baulsupp.cooee.p.single_command
import com.baulsupp.cooee.services.Provider
import com.baulsupp.okurl.credentials.NoToken
import com.baulsupp.okurl.services.github.GithubAuthInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient

class GithubProvider : Provider("github", GithubAuthInterceptor().serviceDefinition) {
  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    if (request.parsed_command == listOf("github")) {
      return flowOf(githubWebsite)
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

//  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
//    override suspend fun suggestCommands(command: String): List<Suggestion> {
//      return projects
//        .map {
//          Suggestion(
//            it.full_name,
//            provider = name,
//            description = it.description ?: "Github: ${it.full_name}",
//            type = SuggestionType.LINK,
//            url = "https://github.com/${it.full_name}"
//          )
//        } + Suggestion(
//        "github",
//        provider = name,
//        description = "Github",
//        type = SuggestionType.LINK,
//        url = "https://github.com"
//      )
//    }
//
//    override suspend fun matches(command: String): Boolean {
//      return this@GithubProvider.matches(command)
//    }
//  }

  override suspend fun matches(command: String): Boolean {
    return command == "github" || projects().any {
      it.full_name == command
    }
  }

  override suspend fun suggest(command: CompletionRequest): List<CompletionSuggestion> {
    return listOf(
        CompletionSuggestion.command(
            CommandSuggestion(provider = "github", description = "Github Website"), "github"),
    ) + projects().map {
      CompletionSuggestion.command(CommandSuggestion(provider = "github",
          description = it.description ?: "Github: ${it.full_name}", command = it.full_name, url = "https://github.com/${it.full_name}"),
          it.full_name)
    }
  }

//  override suspend fun todo(): List<Suggestion> {
//    val cutoff = Instant.now().minus(3, ChronoUnit.DAYS)
//
//    return recentActivePullRequests().filter {
//      it.updatedAt.isAfter(cutoff)
//    }.map {
//      Suggestion(
//        "${it.repository.nameWithOwner}#${it.number}", name, it.title, SuggestionType.LINK,
//        url = it.permalink
//      )
//    }
//  }

  companion object {
    val githubWebsite = CommandResponse.redirect("https://github.com/")
  }
}
