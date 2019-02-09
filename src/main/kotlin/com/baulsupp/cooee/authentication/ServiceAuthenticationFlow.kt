package com.baulsupp.cooee.authentication

import com.baulsupp.cooee.api.AuthenticateCallbackRequest
import com.baulsupp.cooee.api.AuthenticateRequest
import com.baulsupp.okurl.credentials.ServiceDefinition
import io.ktor.application.ApplicationCall

interface ServiceAuthenticationFlow<T> {
  val serviceDefinition: ServiceDefinition<T>

  suspend fun startFlow(state: String, callbackUrl: String, call: ApplicationCall)
  suspend fun completeFlow(request: AuthenticateCallbackRequest, call: ApplicationCall): T
}
