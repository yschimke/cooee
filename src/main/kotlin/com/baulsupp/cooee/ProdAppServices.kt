package com.baulsupp.cooee

import com.baulsupp.cooee.mongo.MongoCredentialsStore
import com.baulsupp.cooee.mongo.MongoProviderStore
import com.baulsupp.cooee.mongo.MongoUserStore
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.providers.github.GithubProvider
import com.baulsupp.cooee.providers.google.GoogleProvider
import com.baulsupp.cooee.providers.jira.JiraProvider
import com.baulsupp.cooee.providers.twitter.TwitterProvider
import com.baulsupp.cooee.users.JwtUserAuthenticator
import io.ktor.application.ApplicationCall
import okhttp3.EventListener
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener

class ProdAppServices(val local: Boolean): AppServices {
  override fun close() {
    client.connectionPool().evictAll()
    client.dispatcher().executorService().shutdown()
  }

  override val client = run {
    val httpListener = if (local) {
    LoggingEventListener.Factory { s -> println(s) }
  } else {
    EventListener.Factory { EventListener.NONE }
  }
    OkHttpClient.Builder().eventListenerFactory(httpListener).build()
  }

  override val providerStore = MongoProviderStore(this::defaultProviders)

  override val userStore = MongoUserStore()

  override val userAuthenticator = JwtUserAuthenticator(userStore)

  override fun defaultProviders() = listOf(
    GoogleProvider(),
    JiraProvider("https://jira.atlassian.com/", client),
    GithubProvider,
    TwitterProvider(client),
    BookmarksProvider()
  )

  override val userServices = object: UserServices {
    override fun credentialsStore(user: String) = MongoCredentialsStore(user)

    override suspend fun providersFor(call: ApplicationCall): RegistryProvider =
      userAuthenticator.userForRequest(call)?.let { providerStore.forUser(it) } ?: RegistryProvider(defaultProviders())
  }
}
