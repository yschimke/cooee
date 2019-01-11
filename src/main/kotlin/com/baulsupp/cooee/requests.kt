package com.baulsupp.cooee

import com.baulsupp.cooee.api.*
import com.baulsupp.cooee.mongo.StringService
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.users.JwtUserAuthenticator
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
    is Unmatched -> call.respond(HttpStatusCode.NotFound)
    is Completed -> call.respond(r)
  }
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.commandCompletionApi(
  commandQuery: CommandCompletion,
  registryProvider: RegistryProvider
) {
  val command = commandQuery.q ?: ""

  val filtered = commandCompletion(registryProvider, command)

  call.respond(filtered)
}

private suspend fun commandCompletion(
  registryProvider: RegistryProvider,
  command: String
): Completions {
  return Completions(registryProvider.commandCompleter().suggestCommands(command))
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.argumentCompletionApi(
  argumentQuery: ArgumentCompletion,
  registryProvider: RegistryProvider
) {
  call.respond(argumentCompletion(registryProvider, argumentQuery.command!!, argumentQuery.args))
}

private suspend fun argumentCompletion(
  registryProvider: RegistryProvider,
  command: String,
  arguments: List<String>
): Completions {
  return Completions(registryProvider.argumentCompleter().suggestArguments(command, arguments)?.map { "$command $it" }.orEmpty())
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
  val q = it.q ?: ""

  // TODO smarter split
  val query = q.split(" ")

  val results = when {
    query.isEmpty() -> Completions(listOf())
    query.size == 1 -> commandCompletion(registryProvider, query.first())
    else -> argumentCompletion(registryProvider, query.first(), query.drop(1))
  }

  val response: SearchSuggestionsResults =
    SearchSuggestionsResults(
      q,
      results.completion_list.map { it.completion },
      results.completion_list.map { it.description },
      results.completion_list.map { "https://coo.ee/go?q=${it.completion.replace(" ", "+")}" })

  call.respond(response)
}
