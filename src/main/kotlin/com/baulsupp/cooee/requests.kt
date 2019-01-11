package com.baulsupp.cooee

import com.baulsupp.cooee.api.*
import com.baulsupp.cooee.mongo.StringService
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.credentials.CredentialsStore
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
  registryProvider: RegistryProvider
) {
  val r =
    goInfo.command?.let { registryProvider.go(it, *goInfo.args.toTypedArray()) } ?: Unmatched

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
suspend fun PipelineContext<Unit, ApplicationCall>.bounceWeb(
  go: Go,
  registryProvider: RegistryProvider
) {
  val r =
    go.command?.let { registryProvider.go(it, *go.args.toTypedArray()) } ?: Unmatched

  when (r) {
    is RedirectResult -> call.respondRedirect(r.location, permanent = false)
    is Unmatched -> call.respond("Not Found Page")
    is Completed -> call.respond(r)
  }
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.completionApi(
  commandQuery: CompletionRequest,
  registryProvider: RegistryProvider
) {
  val completions =
    if (commandQuery.isCommand()) commandCompletion(registryProvider, commandQuery) else argumentCompletion(
      registryProvider,
      commandQuery
    )

  call.respond(completions)
}

@KtorExperimentalLocationsAPI
private suspend fun commandCompletion(
  registryProvider: RegistryProvider,
  command: CompletionRequest
): Completions {
  val commands = registryProvider.commandCompleter().suggestCommands(command.command)
  return Completions(commands.map { CompletionItem(it, it, "Command for '$it'") })
}

@KtorExperimentalLocationsAPI
private suspend fun argumentCompletion(
  registryProvider: RegistryProvider,
  command: CompletionRequest
): Completions {
  val suggestArguments = registryProvider.argumentCompleter().suggestArguments(
    command.command,
    command.args
  )
  val commands = suggestArguments.orEmpty()
  return Completions.complete(command, commands)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.authorize(
  authorize: Authorize,
  user: UserEntry,
  credentialsStore: CredentialsStore
) {
  if (authorize.serviceName == null || authorize.token == null) {
    throw BadRequestException()
  }

  credentialsStore.set(StringService(authorize.serviceName), user.user, authorize.token)
  call.respond(HttpStatusCode.Created)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.searchSuggestion(
  it: SearchSuggestion,
  registryProvider: RegistryProvider
) {
  val query = CompletionRequest(it.q ?: "")

  val results = when {
    query.isCommand() -> commandCompletion(registryProvider, query)
    else -> argumentCompletion(registryProvider, query)
  }

  val response: SearchSuggestionsResults =
    SearchSuggestionsResults(
      it.q ?: "",
      results.completions.map { it.line },
      results.completions.map { it.description },
      results.completions.map { "https://coo.ee/go?q=${it.line.replace(" ", "+")}" })

  call.respond(response)
}
