package com.baulsupp.cooee.test

import com.baulsupp.cooee.authentication.AuthenticationData
import com.baulsupp.cooee.authentication.AuthenticationFlowCache
import com.baulsupp.cooee.authentication.AuthenticationFlowInstance

class TestAuthenticationFlowCache : AuthenticationFlowCache {
  val instances = mutableMapOf<String, AuthenticationFlowInstance>()
  val data = mutableMapOf<String, AuthenticationData>()

  override suspend fun store(authenticationFlowInstance: AuthenticationFlowInstance) {
    instances[authenticationFlowInstance.state] = authenticationFlowInstance
  }

  override suspend fun find(state: String): AuthenticationFlowInstance? {
    return instances[state]
  }

  override suspend fun storeData(authenticationData: AuthenticationData) {
    data[authenticationData.state] = authenticationData
  }

  override suspend fun findData(state: String): AuthenticationData? {
    return data[state]
  }
}
