package com.baulsupp.cooee.test

import com.baulsupp.cooee.authentication.AuthenticationFlowCache
import com.baulsupp.cooee.authentication.AuthenticationFlowInstance

class TestAuthenticationFlowCache : AuthenticationFlowCache {
  val instances = mutableMapOf<String, AuthenticationFlowInstance>()

  override suspend fun store(authenticationFlowInstance: AuthenticationFlowInstance) {
    instances[authenticationFlowInstance.state] = authenticationFlowInstance
  }

  override suspend fun find(state: String): AuthenticationFlowInstance? {
    return instances[state]
  }
}
