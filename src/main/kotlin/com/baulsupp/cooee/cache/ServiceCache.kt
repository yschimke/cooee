package com.baulsupp.cooee.cache

interface ServiceCache {
  fun <T : Any> get(email: String?, providerName: String?, key: String): T?

  fun <T : Any> set(email: String?, providerName: String?, key: String, value: T)
}
