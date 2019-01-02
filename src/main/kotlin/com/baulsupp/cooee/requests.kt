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

  call.respond(r)
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

  val completions = registryProvider.commandCompleter().suggestCommands(command)

  // TODO not needed
  val filtered = completions.filter { it.startsWith(command) }

  call.respond(Completions(filtered))
}

@KtorExperimentalLocationsAPI
suspend fun PipelineContext<Unit, ApplicationCall>.argumentCompletionApi(
  argumentQuery: ArgumentCompletion,
  registryProvider: RegistryProvider
) {
  call.respond(Completions(listOf("close", "comment")))
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
  appServices: AppServices
) {
  val q = it.q

  val result = " [\"$q\",\n" +
    "  [\"$q-A\",\n" +
    "   \"$q-B\",\n" +
    "   \"$q-C\"],\n" +
    "  [\"Desc A\",\n" +
    "   \"Desc B\",\n" +
    "   \"Desc C\"],\n" +
    "  [\"https://coo.ee/go?q=$q-A\",\n" +
    "   \"https://coo.ee/go?q=$q-B\",\n" +
    "   \"https://coo.ee/go?q=$q-C\"]]"

  call.respond(result)
}
