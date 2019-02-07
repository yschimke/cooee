package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.providers.ProviderConfigStore
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoProviderConfigStore(
  private val mongoDb: CoroutineDatabase
) : ProviderConfigStore {
  private val providerDb: CoroutineCollection<ProviderInstance> by lazy {
    mongoDb.getCollection<ProviderInstance>(
      "providers"
    )
  }

  override suspend fun forUser(email: String): List<ProviderInstance> {
    return providerDb.find(eq("email", email)).toList()
  }

  override suspend fun store(email: String, providerName: String, config: Map<String, Any>) {
    val doc = ProviderInstance(email = email, provider = providerName, config = config)

    providerDb.replaceOne(
      and(eq("email", email), eq("provider", providerName)),
      doc,
      ReplaceOptions().upsert(true)
    )
  }

  override suspend fun remove(email: String, providerName: String) {
    providerDb.deleteMany(and(eq("email", email), eq("provider", providerName)))
  }
}
