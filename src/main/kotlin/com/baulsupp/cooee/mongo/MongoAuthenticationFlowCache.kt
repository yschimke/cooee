package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.authentication.AuthenticationFlowCache
import com.baulsupp.cooee.authentication.AuthenticationFlowInstance
import com.mongodb.client.model.Filters
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoAuthenticationFlowCache(private val mongoDb: CoroutineDatabase) : AuthenticationFlowCache {
  override suspend fun store(authenticationFlowInstance: AuthenticationFlowInstance) {
    instanceDb.insertOne(authenticationFlowInstance)
  }

  override suspend fun find(state: String): AuthenticationFlowInstance? {
    return instanceDb.findOne(Filters.eq("state", state))
  }

  private val instanceDb: CoroutineCollection<AuthenticationFlowInstance> by lazy {
    mongoDb.getCollection<AuthenticationFlowInstance>(
      "authenticationFlow"
    )
  }
}
