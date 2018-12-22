package com.baulsupp.cooee.providers

interface Provider : ProviderFunctions {
  val name: String

  fun configure(instance: ProviderInstance, db: ProviderStore)
}
