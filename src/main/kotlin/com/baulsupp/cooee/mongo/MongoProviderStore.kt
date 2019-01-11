package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.cooee.providers.ProviderStore
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.providers.ProvidersProvider
import com.baulsupp.cooee.reactor.awaitList
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.Document

class MongoProviderStore(
  private val providers: () -> List<BaseProvider>,
  private val mongoDb: MongoDatabase,
  private val appServices: AppServices
) : ProviderStore {
  private val providerDb: MongoCollection<Document> by lazy { mongoDb.getCollection("providers") }

  override suspend fun forUser(email: String): RegistryProvider? {
    val providerInstances =
      providerDb.find(eq("user", email), ProviderInstance::class.java).awaitList()

    val providersProvider = ProvidersProvider()
    providersProvider.init(appServices = appServices)
    providersProvider.configure(ProviderInstance(email, "providers", mapOf()))

    val providers = providers().mapNotNull {
      val possibleName = it.name
      val providerConfig = providerInstances.find { config -> config.name == possibleName }

      if (providerConfig != null) {
        it.configure(providerConfig)
        it
      } else {
        null
      }
    } + providersProvider

    return RegistryProvider(providers)
  }

  override suspend fun store(providerInstance: ProviderInstance) {
    val doc =
      Document().append("user", providerInstance.user).append("name", providerInstance.name)
        .append("config", providerInstance.config)

    providerDb.replaceOne(
      and(eq("user", providerInstance.user), eq("name", providerInstance.name)),
      doc,
      ReplaceOptions().upsert(true)
    ).awaitFirst()
  }

  override suspend fun remove(email: String, providerName: String) {
    providerDb.deleteMany(and(eq("user", email), eq("name", providerName))).awaitFirst()
  }
}
