package com.baulsupp.cooee.providers

class TestProviderStore : ProviderStore {
  override suspend fun forUser(user: String): RegistryProvider? {
    return null
  }

  override suspend fun store(providerInstance: ProviderInstance) {
  }

  override suspend fun remove(user: String, name: String) {
  }
}
