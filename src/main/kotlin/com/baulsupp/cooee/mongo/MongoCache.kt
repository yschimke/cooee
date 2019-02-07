package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.cache.ServiceCache
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.*
import java.util.concurrent.TimeUnit.MINUTES

class MongoCache(private val mongoDb: CoroutineDatabase) : ServiceCache {
  private val cacheDb: CoroutineCollection<Document> by lazy {
    mongoDb.getCollection<Document>("cache")
  }

  override suspend fun get(email: String?, providerName: String?, key: String): String? {
    return cacheDb.find(
      and(
        eq("email", email),
        eq("providerName", providerName),
        eq("key", key)
      )
    ).first()?.getString("value")
  }

  override suspend fun set(email: String?, providerName: String?, key: String, value: String) {
    val doc =
      Document().append("email", email).append("providerName", providerName)
        .append("key", key).append("value", value).append("lastUpdated", Date())

    cacheDb.replaceOne(
      and(
        eq("email", email),
        eq("providerName", providerName),
        eq("key", key)
      ), doc, ReplaceOptions().upsert(true)
    )
  }

  suspend fun createTTLIndex() {
    cacheDb.createIndex(eq("lastUpdated", 1), IndexOptions().expireAfter(60, MINUTES))
  }
}
