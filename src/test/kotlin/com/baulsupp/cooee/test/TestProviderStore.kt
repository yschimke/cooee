package com.baulsupp.cooee.test

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.mongo.ProviderInstance
import com.baulsupp.cooee.providers.ProviderConfigStore

class TestProviderStore(val appServices: AppServices) : ProviderConfigStore {
  val providerInstances = mutableSetOf<ProviderInstance>()

  override suspend fun forUser(email: String): List<ProviderInstance> {
    return providerInstances.filter { it.email == email }
  }

  override suspend fun store(email: String, providerName: String, config: Map<String, Any>) {
    remove(email, providerName)

    providerInstances.add(ProviderInstance(email, providerName, config))
  }

  override suspend fun remove(email: String, providerName: String) {
    providerInstances.removeIf { it.email == email && it.providerName == providerName }
  }
}
