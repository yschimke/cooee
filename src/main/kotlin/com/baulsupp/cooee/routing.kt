package com.baulsupp.cooee

import com.baulsupp.cooee.api.ArgumentCompletion
import com.baulsupp.cooee.api.Authorize
import com.baulsupp.cooee.api.CommandCompletion
import com.baulsupp.cooee.api.Go
import com.baulsupp.cooee.api.GoInfo
import com.baulsupp.cooee.api.Login
import com.baulsupp.cooee.api.UserInfo
import io.jsonwebtoken.JwtException
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.defaultResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.routing.Routing

@KtorExperimentalLocationsAPI
fun Routing.root(appServices: AppServices) {
  get<Login> { loginWeb(it, appServices.userStore) }
  get<Go> { bounceWeb(it, appServices.userServices.providersFor(call)) }
  get<GoInfo> { bounceApi(it, appServices.userServices.providersFor(call)) }
  get<UserInfo> { userApi(appServices.userAuthenticator.userForRequest(call), appServices.userStore) }
  get<CommandCompletion> { commandCompletionApi(it, appServices.userServices.providersFor(call)) }
  get<ArgumentCompletion> { argumentCompletionApi(it, appServices.userServices.providersFor(call)) }
  post<Authorize> {
    val user = appServices.userAuthenticator.userForRequest(call) ?: throw AuthenticationException()
    authorize(it, user, appServices.userServices.credentialsStore(user))
  }

  install(StatusPages) {
    exception<JwtException> { cause ->
      application.log.warn("jwt error", cause)
      call.respond(HttpStatusCode.BadRequest)
    }
    exception<AuthenticationException> { call.respond(HttpStatusCode.Unauthorized) }
    exception<AuthorizationException> { call.respond(HttpStatusCode.Forbidden) }
    exception<BadRequestException> { call.respond(HttpStatusCode.BadRequest) }
    exception<Exception> { x ->
      application.log.warn("Failed", x)
      call.respond(HttpStatusCode.InternalServerError, x.toString())
    }
  }

  static {
    resources("static")
    defaultResource("static/index.html")
  }

}
