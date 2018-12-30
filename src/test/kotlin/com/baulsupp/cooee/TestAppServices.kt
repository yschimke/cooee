package com.baulsupp.cooee

import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.providers.github.GithubProvider
import com.baulsupp.cooee.providers.google.GoogleProvider
import com.baulsupp.cooee.providers.jira.JiraProvider
import com.baulsupp.cooee.providers.twitter.TwitterProvider
import com.baulsupp.cooee.test.TestProviderStore
import com.baulsupp.cooee.test.TestUserStore
import com.baulsupp.cooee.users.TestUserAuthenticator
import com.baulsupp.okurl.credentials.InMemoryCredentialsStore
import io.ktor.application.ApplicationCall
import okhttp3.OkHttpClient

class TestAppServices() : AppServices {
  override fun close() {
    client.connectionPool().evictAll()
    client.dispatcher().executorService().shutdown()
  }

  override val client = OkHttpClient()

  override val providerStore =
    TestProviderStore { com.baulsupp.cooee.providers.defaultProviders(client) }

  override val userStore = TestUserStore()

  override val userAuthenticator = TestUserAuthenticator()

  override val userServices = object : UserServices {
    override fun credentialsStore(user: String) = InMemoryCredentialsStore()

    override suspend fun providersFor(call: ApplicationCall): RegistryProvider =
      userAuthenticator.userForRequest(call)?.let { providerStore.forUser(it) } ?: RegistryProvider(defaultProviders())
  }

  override fun defaultProviders() = listOf(
    GoogleProvider(),
    JiraProvider("https://jira.atlassian.com/", client),
    GithubProvider,
    TwitterProvider(client),
    BookmarksProvider()
  )
}
