package com.baulsupp.cooee.cache

import io.ktor.util.InternalAPI
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.codec.Utf8StringCodec
import kotlinx.coroutines.future.await

@InternalAPI
class RedisCache(val url: String) : ServiceCache {
  private val db by lazy {
    RedisClient.create().connectAsync(Utf8StringCodec(), RedisURI.create(url))
  }

  override suspend fun get(email: String?, providerName: String?, key: String): String {
    val con = db.await()

    val result = con.async()[key(email, providerName, key)]

    return result.await()
  }

  private fun key(email: String?, providerName: String?, key: String) =
    "$email:$providerName:$key"

  override suspend fun set(email: String?, providerName: String?, key: String, value: String) {
    val con = db.await()

    con.async().set(key(email, providerName, key), value).await()
  }
}

//@InternalAPI
//suspend fun main(args: Array<String>) {
//  val r = RedisCache("redis://localhost:6379/0")
//  r.set("a", "b", "c", "d")
//  println(r.get("a", "b", "c"))
//}
