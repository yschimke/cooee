package com.baulsupp.cooee.cache

import com.baulsupp.okurl.kotlin.moshi

class MoshiTypedCache(val cache: ServiceCache) {
  suspend inline fun <reified T> get(
    email: String?,
    providerName: String?,
    key: String,
    readThrough: () -> T
  ): T {
    val valueString = cache.get(email, providerName, key)
    return when {
      valueString != null -> moshi.adapter(T::class.java).fromJson(valueString)!!
      else -> {
        val value = readThrough()

        // TODO consider a sentinel value for negative caching
        if (value != null) {
          set(email, providerName, key, value)
        }

        value
      }
    }
  }

  suspend inline fun <reified T> set(email: String?, providerName: String?, key: String, value: T) {
    val valueString = moshi.adapter(T::class.java).toJson(value)
    cache.set(email, providerName, key, valueString)
  }
}
