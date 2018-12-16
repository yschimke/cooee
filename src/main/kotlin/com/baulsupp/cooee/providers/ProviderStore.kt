package com.baulsupp.cooee.providers

interface ProviderStore {
  suspend fun forUser(user: String): RegistryProvider?
}
