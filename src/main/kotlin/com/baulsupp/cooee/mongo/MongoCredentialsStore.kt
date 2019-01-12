package com.baulsupp.cooee.mongo

import com.baulsupp.okurl.credentials.CredentialsStore
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.services.AbstractServiceDefinition
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitLast
import org.bson.Document

class MongoCredentialsStore(private val mongoDb: MongoDatabase) : CredentialsStore {
  private val credentialsDb: MongoCollection<Document> by lazy { mongoDb.getCollection("credentials") }

  override suspend fun <T> get(serviceDefinition: ServiceDefinition<T>, tokenSet: String): T? {
    val token = credentialsDb.find(
      and(
        eq("user", tokenSet),
        eq("serviceName", serviceDefinition.shortName())
      ), UserCredentials::class.java
    ).awaitFirstOrNull()

    return token?.let { serviceDefinition.parseCredentialsString(it.token) }
  }

  override suspend fun <T> remove(serviceDefinition: ServiceDefinition<T>, tokenSet: String) {
    credentialsDb.deleteMany(
      and(
        eq("user", tokenSet),
        eq("serviceName", serviceDefinition.shortName())
      )
    )
  }

  override suspend fun <T> set(serviceDefinition: ServiceDefinition<T>, tokenSet: String, credentials: T) {
    val token = serviceDefinition.formatCredentialsString(credentials)

    val doc =
      Document().append("token", token).append("user", tokenSet)
        .append("serviceName", serviceDefinition.shortName())

    credentialsDb.replaceOne(
      and(
        eq("user", tokenSet),
        eq("serviceName", serviceDefinition.shortName())
      ), doc, ReplaceOptions().upsert(true)
    ).awaitLast()
  }
}

class StringService(name: String) : AbstractServiceDefinition<String>("test.com", name, name) {
  override fun formatCredentialsString(credentials: String): String = credentials
  override fun parseCredentialsString(s: String): String = s
}
