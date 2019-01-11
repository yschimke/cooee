package com.baulsupp.cooee.test

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.providers.Provider
import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.cooee.providers.ProviderStore
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.providers.ProvidersProvider

class TestProviderStore(val appServices: AppServices, private val providers: () -> List<Provider>) : ProviderStore {
  val providerInstances = mutableSetOf<ProviderInstance>()

  override suspend fun forUser(email: String): RegistryProvider? {
    val providersProvider = ProvidersProvider().apply {
      init(this@TestProviderStore.appServices)
      configure(ProviderInstance(email, "providers", mapOf()))
    }

    val userProviders = providers().mapNotNull { p ->
      val config = providerInstances.find { pi ->
        p.name == pi.name && pi.user == email
      }

      if (config != null) {
        p.apply { configure(config) }
      } else {
        null
      }
    } + providersProvider

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
