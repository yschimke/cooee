package com.baulsupp.cooee.test

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.UserServices
import com.baulsupp.cooee.okhttp.close
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.providers.github.GithubProvider
import com.baulsupp.cooee.providers.google.GoogleProvider
import com.baulsupp.cooee.providers.jira.JiraProvider
import com.baulsupp.cooee.providers.twitter.TwitterProvider
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.credentials.CredentialsStore
import com.baulsupp.okurl.credentials.InMemoryCredentialsStore
import io.ktor.application.ApplicationCall
import io.ktor.application.log
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import org.slf4j.LoggerFactory

class TestAppServices : AppServices {
  override fun close() {
    // TODO tearing down hear causes the request to
//    client.close()
  }

  val log = LoggerFactory.getLogger(this::class.java)

  override val providerStore =
    TestProviderStore(this) { defaultProviders() }

  override val userStore = TestUserStore()

  override val userAuthenticator = TestUserAuthenticator()

  override val credentialsStore = InMemoryCredentialsStore()

  override val client: OkHttpClient = OkHttpClient.Builder().apply {
    eventListenerFactory(LoggingEventListener.Factory { s -> log.info(s) })
    addNetworkInterceptor(AuthenticatingInterceptor(credentialsStore))
  }.build()

  override val userServices = object : UserServices {
    override suspend fun providersFor(call: ApplicationCall): RegistryProvider =
      userAuthenticator.userForRequest(call)?.let { providerStore.forUser(it) } ?: RegistryProvider(defaultProviders())
  }

  override fun defaultProviders() = listOf(
    GoogleProvider(),
    JiraProvider("https://jira.atlassian.com/"),
    GithubProvider(),
    TwitterProvider(),
    BookmarksProvider()
  ).onEach { it.init(this) }
}
