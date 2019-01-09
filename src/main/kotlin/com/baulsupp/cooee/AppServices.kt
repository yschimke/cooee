package com.baulsupp.cooee

import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.providers.ProviderStore
import com.baulsupp.cooee.users.UserAuthenticator
import com.baulsupp.okurl.credentials.CredentialsStore
import okhttp3.OkHttpClient

interface AppServices : AutoCloseable {
  fun defaultProviders(): List<BaseProvider>
  val client: OkHttpClient
  val providerStore: ProviderStore
  val userAuthenticator: UserAuthenticator
  val userServices: UserServices
  val credentialsStore: CredentialsStore
  override fun close()
}
