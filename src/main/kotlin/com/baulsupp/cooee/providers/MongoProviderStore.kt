package com.baulsupp.cooee.providers

import com.baulsupp.cooee.mongo.MongoFactory
import com.baulsupp.cooee.reactor.awaitList
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections.include
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document

class MongoProviderStore(val registry: RegistryProvider) : ProviderStore {
  val providerDb: MongoCollection<Document> by lazy { MongoFactory.mongoDb().getCollection("providers") }

  override suspend fun forUser(user: String): RegistryProvider? {
    val providerNames =
      providerDb.find(eq("user", user)).projection(include("provider")).awaitList().map { it.getString("provider") }

    return RegistryProvider(registry.providers.filter { providerNames.contains(it.name) })
  }
}
