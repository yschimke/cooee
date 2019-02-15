package com.baulsupp.cooee.authentication

interface AuthenticationFlowCache {
  suspend fun store(authenticationFlowInstance: AuthenticationFlowInstance)
  suspend fun find(state: String): AuthenticationFlowInstance?

  suspend fun storeData(authenticationData: AuthenticationData)
  suspend fun findData(state: String): AuthenticationData?
}
