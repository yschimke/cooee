package com.baulsupp.cooee.authentication

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.AuthenticateCallbackRequest
import com.baulsupp.cooee.api.AuthenticateRequest
import com.baulsupp.cooee.providers.strava.StravaAuthenticationFlow
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import java.util.*
import kotlin.reflect.full.createInstance

class ProdAuthenticationFlow(val appServices: AppServices) : AuthenticationFlow {
  val services = mapOf("strava" to StravaAuthenticationFlow::class)

  // TODO fix callback
  val redirectUri = "http://localhost:8080/web/callback"

  override suspend fun startFlow(request: AuthenticateRequest, call: ApplicationCall) {
    val serviceFlow = serviceFlow(request.service)

    if (serviceFlow != null) {
      val state = UUID.randomUUID().toString()

      appServices.authenticationFlowCache.store(AuthenticationFlowInstance(state, request.token ?: "X", request.service))

      serviceFlow.startFlow(state, redirectUri, call)
    } else {
      call.respond(HttpStatusCode.NotFound)
    }
  }

  private fun serviceFlow(service: String) = services[service]?.createInstance()

  override suspend fun completeFlow(request: AuthenticateCallbackRequest, call: ApplicationCall) {
    val flowInstance = appServices.authenticationFlowCache.find(request.state ?: "X")

    if (flowInstance != null) {
      val serviceFlow = serviceFlow(flowInstance.service)

      if (serviceFlow != null) {
        val credentials = serviceFlow.completeFlow(request, call)

        appServices.credentialsStore.set(serviceFlow.serviceDefinition, "yuri@coo.ee", credentials)
      }
    }
  }
}
