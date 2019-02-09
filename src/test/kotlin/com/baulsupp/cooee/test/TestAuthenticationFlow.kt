package com.baulsupp.cooee.test

import com.baulsupp.cooee.api.AuthenticateCallbackRequest
import com.baulsupp.cooee.api.AuthenticateRequest
import com.baulsupp.cooee.authentication.AuthenticationFlow
import io.ktor.application.ApplicationCall

class TestAuthenticationFlow : AuthenticationFlow {
  override suspend fun startFlow(request: AuthenticateRequest, call: ApplicationCall) {
    TODO("not implemented")
  }

  override suspend fun completeFlow(request: AuthenticateCallbackRequest, call: ApplicationCall) {
    TODO("not implemented")
  }
}
