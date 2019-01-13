@file:Suppress("UNCHECKED_CAST")

package com.baulsupp.cooee.cache

import java.util.concurrent.ConcurrentHashMap

class LocalCache : ServiceCache {
  val map = ConcurrentHashMap<String, Any>()

  override fun <T : Any> get(email: String?, providerName: String?, key: String): T? {
    return map["$email:$key"] as? T
  }

  override fun <T : Any> set(email: String?, providerName: String?, key: String, value: T) {
    map["$email:$key"] = value
  }
}
