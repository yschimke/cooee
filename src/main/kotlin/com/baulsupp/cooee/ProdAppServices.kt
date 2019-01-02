package com.baulsupp.cooee

import com.baulsupp.cooee.mongo.MongoCredentialsStore
import com.baulsupp.cooee.mongo.MongoFactory
import com.baulsupp.cooee.mongo.MongoProviderStore
import com.baulsupp.cooee.mongo.MongoUserStore
import com.baulsupp.cooee.okhttp.close
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.providers.github.GithubProvider
import com.baulsupp.cooee.providers.google.GoogleProvider
import com.baulsupp.cooee.providers.jira.JiraProvider
import com.baulsupp.cooee.providers.strava.StravaProvider
import com.baulsupp.cooee.providers.twitter.TwitterProvider
import com.baulsupp.cooee.users.JwtUserAuthenticator
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.mongodb.reactivestreams.client.MongoDatabase
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.log
import io.netty.channel.nio.NioEventLoopGroup
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import org.slf4j.LoggerFactory

class ProdAppServices(application: Application) : AppServices {
  override fun close() {
    client.close()
    mongo.close()
    eventLoop.shutdownGracefully()
  }

  private val eventLoop = NioEventLoopGroup()

  // TODO allow local
  val mongo = MongoFactory.mongo(false, eventLoop)

  val mongoDb: MongoDatabase = mongo.getDatabase("cooee")

  override val providerStore = MongoProviderStore(this::defaultProviders, mongoDb, this)

  override val userStore = MongoUserStore(mongoDb)

  override val userAuthenticator = JwtUserAuthenticator(userStore)

  override val credentialsStore = MongoCredentialsStore(mongoDb)

  override val client: OkHttpClient = OkHttpClient.Builder().apply {
    eventListenerFactory(LoggingEventListener.Factory { s -> application.log.debug(s) })
    addNetworkInterceptor(AuthenticatingInterceptor(credentialsStore))
  }.build()

  override fun defaultProviders() = listOf(
    GoogleProvider(),
    JiraProvider("https://jira.atlassian.com/"),
    GithubProvider(),
    TwitterProvider(),
    BookmarksProvider(),
    StravaProvider()
  ).onEach { it.init(this) }

  override val userServices = object : UserServices {
    override suspend fun providersFor(call: ApplicationCall): RegistryProvider =
      userAuthenticator.userForRequest(call)?.let { providerStore.forUser(it) } ?: RegistryProvider(defaultProviders())
  }
}
