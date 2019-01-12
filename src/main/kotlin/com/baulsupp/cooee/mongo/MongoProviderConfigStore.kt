package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.providers.ProviderConfigStore
import com.baulsupp.cooee.reactor.awaitList
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.Document

class MongoProviderConfigStore(
  private val mongoDb: MongoDatabase
) : ProviderConfigStore {
  private val providerDb: MongoCollection<Document> by lazy { mongoDb.getCollection("providers") }

  override suspend fun forUser(email: String): List<ProviderInstance> {
    return providerDb.find(eq("user", email), ProviderInstance::class.java).awaitList()
  }

  override suspend fun store(email: String, providerName: String, config: Map<String, Any>) {
    val doc =
      Document().append("email", email).append("name", providerName)
        .append("config", config)

    providerDb.replaceOne(
      and(eq("email", email), eq("name", providerName)),
      doc,
      ReplaceOptions().upsert(true)
    ).awaitFirst()
  }

  override suspend fun remove(email: String, providerName: String) {
    providerDb.deleteMany(and(eq("email", email), eq("name", providerName))).awaitFirst()
  }
}
