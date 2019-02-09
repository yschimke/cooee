package com.baulsupp.cooee.providers.strava

import com.baulsupp.cooee.api.AuthenticateCallbackRequest
import com.baulsupp.cooee.authentication.ServiceAuthenticationFlow
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Token
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import io.ktor.application.ApplicationCall
import io.ktor.response.respondRedirect

class StravaAuthenticationFlow : ServiceAuthenticationFlow<Oauth2Token> {
  override val serviceDefinition: ServiceDefinition<Oauth2Token> = StravaAuthInterceptor().serviceDefinition

  override suspend fun startFlow(state: String, callbackUrl: String, call: ApplicationCall) {
    val clientId = "31260"
    val scopes = listOf(
      "read_all",
      "profile:read_all",
      "profile:write",
      "activity:read_all",
      "activity:write"
    )
    val scopeString = scopes.joinToString(",")
    call.respondRedirect("https://www.strava.com/oauth/authorize?state=$state&client_id=$clientId&redirect_uri=${callbackUrl}&response_type=code&scope=$scopeString")
  }

  override suspend fun completeFlow(request: AuthenticateCallbackRequest, call: ApplicationCall): Oauth2Token {
    return Oauth2Token(request.code!!, "refresh", "31260", "0cf3ecbf9d82ac4e190c0ffbf634e720c2853c63")
  }
}
