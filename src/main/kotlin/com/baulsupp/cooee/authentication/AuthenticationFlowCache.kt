package com.baulsupp.cooee.authentication

interface AuthenticationFlowCache {
  suspend fun store(authenticationFlowInstance: AuthenticationFlowInstance)
  suspend fun find(state: String): AuthenticationFlowInstance?
}
