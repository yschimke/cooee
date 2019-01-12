package com.baulsupp.cooee.providers

import com.baulsupp.cooee.mongo.ProviderInstance

interface ProviderConfigStore {
  suspend fun forUser(email: String): List<ProviderInstance>
  suspend fun store(email: String, providerName: String, config: Map<String, Any>)
  suspend fun remove(email: String, providerName: String)
}
