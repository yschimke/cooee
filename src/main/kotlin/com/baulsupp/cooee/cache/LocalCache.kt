@file:Suppress("UNCHECKED_CAST")

package com.baulsupp.cooee.cache

import java.util.concurrent.ConcurrentHashMap

class LocalCache : ServiceCache {
  val map = ConcurrentHashMap<String, String>()

  override suspend fun get(email: String?, providerName: String?, key: String): String? {
    return map["$email:$providerName:$key"]
  }

  override suspend fun set(email: String?, providerName: String?, key: String, value: String) {
    map["$email:$providerName:$key"] = value

    println(map)
  }
}
