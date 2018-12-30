package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.cooee.users.UserStore
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document

class MongoUserStore(private val mongoDb: MongoDatabase) : UserStore {
  val userDb: MongoCollection<Document> by lazy { mongoDb.getCollection("users") }

  override suspend fun userInfo(user: String): UserEntry? {
    return userDb.find(eq("user", user), UserEntry::class.java).awaitFirstOrNull()
  }

  override suspend fun storeUser(userEntry: UserEntry) {
    val doc =
      Document().append("token", userEntry.token).append("user", userEntry.user).append("email", userEntry.email)

    val result = userDb.replaceOne(eq("token", userEntry.token), doc, ReplaceOptions().upsert(true)).awaitFirst()
  }
}
