@file:Suppress("UNUSED_PARAMETER", "EXPERIMENTAL_API_USAGE")

package com.baulsupp.cooee

import com.baulsupp.cooee.authentication.ProdAuthenticationFlow
import com.baulsupp.cooee.cache.MoshiTypedCache
import com.baulsupp.cooee.features.CooeeFF4jProvider
import com.baulsupp.cooee.features.FF4jFeatureCheck
import com.baulsupp.cooee.features.FeatureCheck
import com.baulsupp.cooee.mongo.*
import com.baulsupp.cooee.okhttp.close
import com.baulsupp.cooee.providers.ProviderRegistry
import com.baulsupp.cooee.users.JwtUserAuthenticator
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.authenticator.RenewingInterceptor
import io.ktor.application.Application
import io.netty.channel.nio.NioEventLoopGroup
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import org.ff4j.cache.InMemoryCacheManager
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProdAppServices(val application: Application) : AppServices {
  override fun close() {
    client.close()
    mongo.close()
    eventLoop.shutdownGracefully()
  }

  override val apiHost = application.environment.config.propertyOrNull("apiHost")?.getString() ?: "api.coo.ee"

  override val wwwHost = application.environment.config.propertyOrNull("wwwHost")?.getString() ?: "www.coo.ee"

  private val eventLoop = NioEventLoopGroup()

  override val services = AuthenticatingInterceptor.defaultServices()

  // TODO allow local
  private val mongo = MongoFactory.mongo(false, eventLoop)

  private val mongoDb: CoroutineDatabase = mongo.getDatabase("cooee")

  override val providerConfigStore = MongoProviderConfigStore(mongoDb)

  override val userAuthenticator = JwtUserAuthenticator()

  override val credentialsStore = MongoCredentialsStore(mongoDb)

  override val providerRegistry = ProviderRegistry(this)

  override val authenticationFlow = ProdAuthenticationFlow(this)

  override val authenticationFlowCache = MongoAuthenticationFlowCache(mongoDb)

  override val cache = MoshiTypedCache(MongoCache(mongoDb))

  override val config = application.environment.config

  val featureSwitches = CooeeFF4jProvider.ff4j.apply {
    isAutocreate = true
    val client = MongoFactory.mongo(false, eventLoop)
    val db = client.getDatabase("cooee")
    featureStore = MongoFeatureStore(db)
    propertiesStore = MongoFeaturePropertyStore(db)

    // TODO replace with event based updates to cache in Mongo
    cache(InMemoryCacheManager())
  }

  override fun featureChecks(userEntry: UserEntry?): FeatureCheck {
    return FF4jFeatureCheck(featureSwitches, userEntry)
  }

  override val client: OkHttpClient = OkHttpClient.Builder().apply {
    eventListenerFactory(LoggingEventListener.Factory { s -> logger.info(s) })
    addInterceptor(RenewingInterceptor(credentialsStore, services))
    addNetworkInterceptor(AuthenticatingInterceptor(credentialsStore, services))
  }.build()

  companion object {
    val logger: Logger = LoggerFactory.getLogger(ProdAppServices::class.java)
  }
}
