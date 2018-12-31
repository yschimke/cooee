package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices

interface Provider : ProviderFunctions {
  val name: String

  fun init(appServices: AppServices)

  fun configure(instance: ProviderInstance)
}
