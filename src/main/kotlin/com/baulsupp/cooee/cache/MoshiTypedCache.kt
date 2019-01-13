package com.baulsupp.cooee.cache

import com.baulsupp.okurl.kotlin.moshi

class MoshiTypedCache(val cache: ServiceCache) {
  suspend inline fun <reified T> get(email: String?, providerName: String?, key: String): T? {
    val valueString = cache.get(email, providerName, key)
    return if (valueString != null) moshi.adapter(T::class.java).fromJson(valueString) else null
  }

  suspend inline fun <reified T> set(email: String?, providerName: String?, key: String, value: T) {
    val valueString = moshi.adapter(T::class.java).toJson(value)
    cache.set(email, providerName, key, valueString)

    // debug code
//    val check = get<T>(email, providerName, key)
//    println(check == value)
//    println(T::class.java)
//    println(check)
//    println(value)
  }
}
