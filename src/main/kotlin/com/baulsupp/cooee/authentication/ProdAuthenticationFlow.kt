package com.baulsupp.cooee.authentication

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.AuthenticateCallbackRequest
import com.baulsupp.cooee.api.AuthenticateRequest
import com.baulsupp.cooee.api.BadRequestException
import com.baulsupp.cooee.providers.strava.StravaAuthenticationFlow
import com.baulsupp.cooee.users.JwtUserAuthenticator
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import java.util.*
import kotlin.reflect.full.createInstance

class ProdAuthenticationFlow(val appServices: AppServices) : AuthenticationFlow {
  val services = mapOf("strava" to StravaAuthenticationFlow::class)

  val redirectUri = appServices.apiUrl("/web/callback")

  override suspend fun startFlow(request: AuthenticateRequest, call: ApplicationCall) {
    val serviceFlow = serviceFlow(request.service)

    if (serviceFlow != null) {
      val state = UUID.randomUUID().toString()

      val token = request.token ?: throw BadRequestException()
      appServices.authenticationFlowCache.store(AuthenticationFlowInstance(state, token, request.service))

      serviceFlow.startFlow(state, redirectUri, call, appServices)
    } else {
      call.respond(HttpStatusCode.NotFound)
    }
  }

  private fun serviceFlow(service: String) = services[service]?.createInstance()

  override suspend fun completeFlow(request: AuthenticateCallbackRequest, call: ApplicationCall) {
    val state = request.state ?: throw BadRequestException()
    val flowInstance = appServices.authenticationFlowCache.find(state)

    if (flowInstance != null) {
      val serviceFlow = serviceFlow(flowInstance.service)
      val user = JwtUserAuthenticator.parseToken(flowInstance.token)

      if (serviceFlow != null && user != null) {
        val credentials = serviceFlow.completeFlow(request, call, appServices)

        appServices.credentialsStore.set(serviceFlow.serviceDefinition, user.email, credentials)

        call.respondRedirect(appServices.wwwUrl("/"))
        return
      }
    }

    call.respond(HttpStatusCode.BadRequest)
  }
}
