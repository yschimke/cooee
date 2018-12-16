package com.baulsupp.cooee.providers

class TestProviderStore: ProviderStore {
  override suspend fun forUser(user: String): RegistryProvider? {
    return null
  }
}
