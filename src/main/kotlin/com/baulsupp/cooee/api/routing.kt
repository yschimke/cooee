package com.baulsupp.cooee.api

import com.baulsupp.cooee.AppServices
import io.jsonwebtoken.JwtException
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.delete
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.locations.put
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing

@KtorExperimentalLocationsAPI
fun Routing.root(appServices: AppServices) {
  get<GoInfo> { bounceApi(it, appServices.providers(call)) }
  get<UserInfo> { userApi(appServices.userAuthenticator.userForRequest(call)) }
  get<CompletionRequest> { completionApi(it, appServices.providers(call)) }
  post<Authorize> {
    val user = appServices.userAuthenticator.userForRequest(call) ?: throw AuthenticationException()
    authorize(it, user, appServices)
  }
  get<SearchSuggestion> {
    searchSuggestion(it, appServices.providers(call))
  }
  get<ProvidersRequest> {
    providersList(appServices, appServices.providers(call))
  }
  get<ProviderRequest> {
    providerRequest(it, appServices, appServices.providers(call))
  }
  delete<ProviderRequest> {
    val user = appServices.userAuthenticator.userForRequest(call) ?: throw AuthenticationException()
    providerDeleteRequest(it, appServices, user)
  }
  put<ProviderRequest> {
    val config = call.receive<ProviderConfig>()
    val user = appServices.userAuthenticator.userForRequest(call) ?: throw AuthenticationException()
    providerConfigRequest(it, config, appServices, user)
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
  }
}
