package com.baulsupp.cooee.cache

interface ServiceCache {
  suspend fun get(email: String?, providerName: String?, key: String): String?

  suspend fun set(email: String?, providerName: String?, key: String, value: String)
}
