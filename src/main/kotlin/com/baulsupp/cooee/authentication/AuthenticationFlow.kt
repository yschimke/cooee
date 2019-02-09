package com.baulsupp.cooee.authentication

import com.baulsupp.cooee.api.AuthenticateCallbackRequest
import com.baulsupp.cooee.api.AuthenticateRequest
import io.ktor.application.ApplicationCall

interface AuthenticationFlow {
  suspend fun startFlow(request: AuthenticateRequest, call: ApplicationCall)
  suspend fun completeFlow(request: AuthenticateCallbackRequest, call: ApplicationCall)
}
