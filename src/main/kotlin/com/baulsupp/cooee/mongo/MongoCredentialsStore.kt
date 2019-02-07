package com.baulsupp.cooee.mongo

import com.baulsupp.okurl.credentials.CredentialsStore
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.services.AbstractServiceDefinition
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoCredentialsStore(private val mongoDb: CoroutineDatabase) : CredentialsStore {
  private val credentialsDb: CoroutineCollection<UserCredentials> by lazy {
    mongoDb.getCollection<UserCredentials>(
      "credentials"
    )
  }

  override suspend fun <T> get(serviceDefinition: ServiceDefinition<T>, tokenSet: String): T? {
    val token = credentialsDb.find(
      and(
        eq("user", tokenSet),
        eq("serviceName", serviceDefinition.shortName())
      )
    ).first()

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

    val doc = UserCredentials(token = token, user = tokenSet, serviceName = serviceDefinition.shortName())

    credentialsDb.replaceOne(
      and(
        eq("user", tokenSet),
        eq("serviceName", serviceDefinition.shortName())
      ), doc, ReplaceOptions().upsert(true)
    )
  }
}

class StringService(name: String) : AbstractServiceDefinition<String>("test.com", name, name) {
  override fun formatCredentialsString(credentials: String): String = credentials
  override fun parseCredentialsString(s: String): String = s
}
