package com.baulsupp.cooee

import com.baulsupp.cooee.api.*
import com.baulsupp.cooee.mongo.StringService
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.users.JwtUserAuthenticator
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.cooee.users.UserStore
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
    goInfo.command?.let { registryProvider.go(it, goInfo.args) } ?: Unmatched

  if (r == Unmatched) {
    call.respond(Completed(message = "no match"))
  } else {
    call.respond(r)
  }
}

suspend fun PipelineContext<Unit, ApplicationCall>.userApi(
  user: String?,
  userStore: UserStore
) {
  if (user == null) {
    throw AuthenticationException()
  }

  val userResult = userStore.userInfo(user) ?: throw AuthorizationException()

  call.respond(userResult)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.loginWeb(
  login: Login,
  userStore: UserStore
) {
  if (login.callback != null && login.user != null) {
    val token = JwtUserAuthenticator.tokenForLogin(login)

    if (token != null) {
      userStore.storeUser(UserEntry(token, login.user, login.email))
      call.respondRedirect(login.callback + "?code=" + token, permanent = false)
    }
  }

  call.respond(HttpStatusCode.Unauthorized)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.bounceWeb(
  go: Go,
  registryProvider: RegistryProvider
) {
  val r =
    go.command?.let { registryProvider.go(it, go.args) } ?: Unmatched

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

  call.respond(Completions(filtered))
}

private suspend fun commandCompletion(
  registryProvider: RegistryProvider,
  command: String
): List<String> {
  val completions = registryProvider.commandCompleter().suggestCommands(command)

  // TODO not needed
  return completions.filter { it.startsWith(command) }
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.argumentCompletionApi(
  argumentQuery: ArgumentCompletion,
  registryProvider: RegistryProvider
) {
  call.respond(Completions(argumentCompletion(registryProvider, argumentQuery.command!!, argumentQuery.args)))
}

private suspend fun argumentCompletion(
  registryProvider: RegistryProvider,
  command: String,
  arguments: List<String>
): List<String> {
  return registryProvider.argumentCompleter().suggestArguments(command, arguments).orEmpty()
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.authorize(
  authorize: Authorize,
  user: String,
  credentialsStore: CredentialsStore
) {
  if (authorize.serviceName == null || authorize.token == null) {
    throw BadRequestException()
  }

  credentialsStore.set(StringService(authorize.serviceName), user, authorize.token)
  call.respond(HttpStatusCode.Created)
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.searchSuggestion(
  it: SearchSuggestion,
  appServices: AppServices,
  registryProvider: RegistryProvider
) {
  val q = it.q ?: ""

  // TODO smarter split
  val query = q.split(" ")

  val results = when {
    query.isEmpty() -> listOf()
    query.size == 1 -> commandCompletion(registryProvider, query.first())
    else -> argumentCompletion(registryProvider, query.first(), query.drop(1)).map { "${query.first()} $it" }
  }

  val response: SearchSuggestionsResults =
    SearchSuggestionsResults(
      q,
      results,
      results.map { "Desc $it" },
      results.map { "https://coo.ee/go?q=${it.replace(" ", "+")}" })

  call.respond(response)
}
