package com.baulsupp.cooee.test

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.cache.MoshiTypedCache
import com.baulsupp.cooee.providers.ProviderRegistry
import com.baulsupp.cooee.users.JwtUserAuthenticator
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.authenticator.RenewingInterceptor
import com.baulsupp.okurl.credentials.InMemoryCredentialsStore
import io.ktor.config.ApplicationConfig
import io.ktor.config.MapApplicationConfig
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import org.slf4j.LoggerFactory

class TestAppServices : AppServices {
  val log = LoggerFactory.getLogger(this::class.java)

  override fun close() {
    // TODO tearing down here causes the request to
//    client.close()
  }

  override val services = AuthenticatingInterceptor.defaultServices()

  override val apiHost = "api.coo.ee"
  override val wwwHost = "www.coo.ee"

  override val providerConfigStore = TestProviderStore(this)

  override val providerRegistry = ProviderRegistry(this, ProviderRegistry.known + ("test" to TestProvider::class))

  override val userAuthenticator = JwtUserAuthenticator()

  override val credentialsStore = InMemoryCredentialsStore()

  override val cache = MoshiTypedCache(LocalCache())

  override val authenticationFlow = TestAuthenticationFlow()

  override val authenticationFlowCache = TestAuthenticationFlowCache()

  override val config = MapApplicationConfig()

  override val client: OkHttpClient = OkHttpClient.Builder().apply {
    val services = AuthenticatingInterceptor.defaultServices()

    eventListenerFactory(LoggingEventListener.Factory { s -> log.info(s) })
    addInterceptor(RenewingInterceptor(credentialsStore, services))
    addNetworkInterceptor(AuthenticatingInterceptor(credentialsStore, services))
  }.build()
}
