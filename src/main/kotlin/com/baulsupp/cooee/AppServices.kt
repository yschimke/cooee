package com.baulsupp.cooee

import com.baulsupp.cooee.cache.MoshiTypedCache
import com.baulsupp.cooee.cache.ServiceCache
import com.baulsupp.cooee.providers.CombinedProvider
import com.baulsupp.cooee.providers.ProviderRegistry
import com.baulsupp.cooee.providers.ProviderConfigStore
import com.baulsupp.cooee.users.UserAuthenticator
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.authenticator.AuthInterceptor
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.credentials.CredentialsStore
import io.ktor.application.ApplicationCall
import okhttp3.OkHttpClient

interface AppServices : AutoCloseable {
  val client: OkHttpClient
  val providerConfigStore: ProviderConfigStore
  val providerRegistry: ProviderRegistry
  val userAuthenticator: UserAuthenticator
  val credentialsStore: CredentialsStore
  val wwwHost: String
  val apiHost: String
  val cache: MoshiTypedCache

  val services: List<AuthInterceptor<*>>

  override fun close()

  fun wwwUrl(path: String): String {
    return if (wwwHost.startsWith("localhost:")) {
      "http://$wwwHost$path"
    } else {
      "https://$wwwHost$path"
    }
  }

  fun apiUrl(path: String): String {
    return if (apiHost.startsWith("localhost:")) {
      "http://$apiHost$path"
    } else {
      "https://$apiHost$path"
    }
  }

  suspend fun userForCall(call: ApplicationCall): UserEntry? = userAuthenticator.userForRequest(call)

  suspend fun providers(call: ApplicationCall): CombinedProvider {
    val user = userForCall(call)
    return providerRegistry.forUser(user).apply { init(this@AppServices, user) }
  }
}
