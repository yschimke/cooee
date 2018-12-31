package com.baulsupp.cooee.test

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.UserServices
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.providers.github.GithubProvider
import com.baulsupp.cooee.providers.google.GoogleProvider
import com.baulsupp.cooee.providers.jira.JiraProvider
import com.baulsupp.cooee.providers.twitter.TwitterProvider
import com.baulsupp.okurl.credentials.InMemoryCredentialsStore
import io.ktor.application.ApplicationCall
import okhttp3.OkHttpClient

class TestAppServices : AppServices {
  override fun close() {
    client.connectionPool().evictAll()
    client.dispatcher().executorService().shutdown()
  }

  override val client = com.baulsupp.okurl.kotlin.client

  override val providerStore =
    TestProviderStore(this) { defaultProviders() }

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
    GithubProvider(),
    TwitterProvider(client),
    BookmarksProvider()
  ).onEach { it.init(this) }
}
