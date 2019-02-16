package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.authentication.AuthenticationData
import com.baulsupp.cooee.authentication.AuthenticationFlowCache
import com.baulsupp.cooee.authentication.AuthenticationFlowInstance
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoAuthenticationFlowCache(private val mongoDb: CoroutineDatabase) : AuthenticationFlowCache {
  override suspend fun store(authenticationFlowInstance: AuthenticationFlowInstance) {
    instanceDb.replaceOne(
      eq("state", authenticationFlowInstance.state),
      authenticationFlowInstance, ReplaceOptions().upsert(true))
  }

  override suspend fun find(state: String): AuthenticationFlowInstance? {
    return instanceDb.findOne(Filters.eq("state", state))
  }

  override suspend fun storeData(authenticationData: AuthenticationData) {
    dataDb.replaceOne(
      eq("state", authenticationData.state),
      authenticationData, ReplaceOptions().upsert(true))
  }

  override suspend fun findData(state: String): AuthenticationData? {
    return dataDb.findOne(Filters.eq("state", state))
  }

  private val instanceDb: CoroutineCollection<AuthenticationFlowInstance> by lazy {
    mongoDb.getCollection<AuthenticationFlowInstance>(
      "authenticationFlow"
    )
  }

  private val dataDb: CoroutineCollection<AuthenticationData> by lazy {
    mongoDb.getCollection<AuthenticationData>(
      "authenticationData"
    )
  }
}
