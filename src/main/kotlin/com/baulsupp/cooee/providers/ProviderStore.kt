package com.baulsupp.cooee.providers

interface ProviderStore {
  suspend fun forUser(email: String): RegistryProvider?
  suspend fun store(providerInstance: ProviderInstance)
  suspend fun remove(email: String, providerName: String)
}
