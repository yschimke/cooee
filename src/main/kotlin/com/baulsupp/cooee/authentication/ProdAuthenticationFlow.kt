@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.baulsupp.cooee.authentication

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.AuthenticateCallbackRequest
import com.baulsupp.cooee.api.AuthenticateRequest
import com.baulsupp.cooee.api.BadRequestException
import com.baulsupp.cooee.users.JwtUserAuthenticator
import com.baulsupp.okurl.authenticator.authflow.Callback
import com.baulsupp.okurl.authenticator.authflow.Prompt
import com.baulsupp.okurl.authenticator.authflow.Scopes
import com.baulsupp.okurl.authenticator.authflow.State
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Flow
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import java.util.*

class ProdAuthenticationFlow(val appServices: AppServices) : AuthenticationFlow {
  val redirectUri = appServices.apiUrl("/web/callback")

  override suspend fun startFlow(request: AuthenticateRequest, call: ApplicationCall) {
    val serviceFlow = serviceFlow(request.service)

    if (serviceFlow != null) {
      val state = UUID.randomUUID().toString()

      serviceFlow.init(appServices.client)

      val token = request.token ?: throw BadRequestException()
      appServices.authenticationFlowCache.store(AuthenticationFlowInstance(state, token, request.service))

      val params = optionParams(serviceFlow, state)
      serviceFlow.defineOptions(params)

      val url = serviceFlow.start()

      call.respondRedirect(url)
    } else {
      call.respond(HttpStatusCode.NotFound)
    }
  }

  private fun optionParams(serviceFlow: Oauth2Flow, state: String): Map<String, Any> {
    val options = serviceFlow.options()

    return options.map {
      val value: Any = when (it) {
        is Prompt -> appServices.config.propertyOrNull(it.param)?.getString() ?: ""
        is Scopes -> appServices.config.propertyOrNull(it.param)?.getList().orEmpty()
        is Callback -> redirectUri
        is State -> state
      }

      it.param to value
    }.toMap()
  }

  private fun serviceFlow(service: String): Oauth2Flow? {
    return appServices.services.find { it.name() == service }?.authFlow() as? Oauth2Flow
  }

  override suspend fun completeFlow(request: AuthenticateCallbackRequest, call: ApplicationCall) {
    val state = request.state ?: throw BadRequestException()
    val flowInstance = appServices.authenticationFlowCache.find(state)

    if (flowInstance != null) {
      val serviceFlow = serviceFlow(flowInstance.service)
      val user = JwtUserAuthenticator.parseToken(flowInstance.token)

      if (serviceFlow != null && user != null && request.code != null) {
        serviceFlow.init(appServices.client)

        val params = optionParams(serviceFlow, state)
        serviceFlow.defineOptions(params)

        val credentials = serviceFlow.complete(request.code)

        appServices.credentialsStore.set(serviceFlow.serviceDefinition, user.email, credentials)

        call.respondRedirect(appServices.wwwUrl("/services"))
        return
      }
    }

    call.respond(HttpStatusCode.BadRequest)
  }
}
