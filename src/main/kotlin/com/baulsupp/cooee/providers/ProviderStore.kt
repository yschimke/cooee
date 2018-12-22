package com.baulsupp.cooee.providers

interface ProviderStore {
  suspend fun forUser(user: String): RegistryProvider?
  suspend fun store(providerInstance: ProviderInstance)
  suspend fun remove(user: String, name: String)
}
