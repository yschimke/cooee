package com.baulsupp.cooee.test

import com.baulsupp.cooee.providers.Provider
import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.cooee.providers.ProviderStore
import com.baulsupp.cooee.providers.RegistryProvider

class TestProviderStore(private val providers: () -> List<Provider>) : ProviderStore {
  private val providerInstances = mutableSetOf<ProviderInstance>()

  override suspend fun forUser(user: String): RegistryProvider? {
    val userProviders = providers().mapNotNull { p ->
      val config = providerInstances.find { pi ->
        p.name == pi.name && pi.user == user
      }

      if (config != null) {
        p.apply { configure(config, this@TestProviderStore) }
      } else {
        null
      }
    }

    return RegistryProvider(userProviders)
  }

  override suspend fun store(providerInstance: ProviderInstance) {
    remove(providerInstance.user, providerInstance.name)

    providerInstances.add(providerInstance)
  }

  override suspend fun remove(user: String, name: String) {
    providerInstances.removeIf { it.user == user && it.name == name }
  }
}
