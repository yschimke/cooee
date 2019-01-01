package com.baulsupp.cooee

import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.providers.ProviderStore
import com.baulsupp.cooee.users.UserAuthenticator
import com.baulsupp.cooee.users.UserStore
import okhttp3.OkHttpClient

interface AppServices : AutoCloseable {
  fun defaultProviders(): List<BaseProvider>
  val client: OkHttpClient
  val providerStore: ProviderStore
  val userStore: UserStore
  val userAuthenticator: UserAuthenticator
  val userServices: UserServices
  override fun close()
}
