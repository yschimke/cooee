package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.users.UserEntry

interface Provider : ProviderFunctions {
  val name: String

  val config: MutableMap<String, Any>

  suspend fun init(appServices: AppServices, user: UserEntry?)

  fun configure(config: Map<String, Any>)
}
