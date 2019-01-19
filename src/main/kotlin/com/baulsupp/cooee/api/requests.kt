package com.baulsupp.cooee.api

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.mongo.StringService
import com.baulsupp.cooee.providers.CombinedProvider
import com.baulsupp.cooee.users.UserEntry
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.util.pipeline.PipelineContext

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.bounceApi(
  goInfo: GoInfo,
  providers: CombinedProvider
) {
  val r =
    goInfo.command?.let { providers.go(it, *goInfo.args.toTypedArray()) } ?: Unmatched

  if (r == Unmatched) {
    call.respond(Completed(message = "no match"))
  } else {
    call.respond(r)
  }
}

suspend fun PipelineContext<Unit, ApplicationCall>.userApi(
  user: UserEntry?
) {
  if (user == null) {
    throw AuthenticationException()
  }

  call.respond(user)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.completionApi(
  commandQuery: CompletionRequest,
  providers: CombinedProvider
) {
  val completions = if (commandQuery.isCommand()) {
    commandCompletion(providers, commandQuery)
  } else {
    argumentCompletion(providers, commandQuery)
  }

  call.respond(completions)
}

@KtorExperimentalLocationsAPI
private suspend fun commandCompletion(
  providers: CombinedProvider,
  command: CompletionRequest
): Completions {
  val commands = providers.commandCompleter().suggestCommands(command.command)
  return Completions(commands.map {
    CompletionItem(
      word = it.completion,
      line = it.completion,
      description = "Command for '${it.completion}'",
      provider = it.provider ?: "unknown"
    )
  })
}

@KtorExperimentalLocationsAPI
private suspend fun argumentCompletion(
  providers: CombinedProvider,
  command: CompletionRequest
): Completions {
  val suggestArguments = providers.argumentCompleter().suggestArguments(command.command, command.args)
  return Completions.complete(command, suggestArguments)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.authorize(
  authorize: Authorize,
  user: UserEntry,
  appServices: AppServices
) {
  if (authorize.serviceName == null || authorize.token == null) {
    throw BadRequestException()
  }

  appServices.credentialsStore.set(StringService(authorize.serviceName), user.email, authorize.token)
  call.respond(HttpStatusCode.Created)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.searchSuggestion(
  it: SearchSuggestion,
  providers: CombinedProvider
) {
  val query = CompletionRequest(it.q ?: "")

  val results = when {
    query.isCommand() -> commandCompletion(providers, query)
    else -> argumentCompletion(providers, query)
  }

  val response =
    SearchSuggestionsResults(
      it.q ?: "",
      results.completions.map { it.line },
      results.completions.map { it.description },
      results.completions.map { "https://coo.ee/go?q=${it.line.replace(" ", "+")}" })

  call.respond(response)
}
