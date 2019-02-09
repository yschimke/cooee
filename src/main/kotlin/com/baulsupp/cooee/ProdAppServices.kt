@file:Suppress("UNUSED_PARAMETER")

package com.baulsupp.cooee

import com.baulsupp.cooee.authentication.ProdAuthenticationFlow
import com.baulsupp.cooee.cache.MoshiTypedCache
import com.baulsupp.cooee.mongo.*
import com.baulsupp.cooee.okhttp.close
import com.baulsupp.cooee.providers.ProviderRegistry
import com.baulsupp.cooee.users.JwtUserAuthenticator
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.authenticator.RenewingInterceptor
import io.ktor.application.Application
import io.netty.channel.nio.NioEventLoopGroup
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProdAppServices(application: Application) : AppServices {
  override fun close() {
    client.close()
    mongo.close()
    eventLoop.shutdownGracefully()
  }

  // TODO fix for localhost
  override val apiHost = "api.coo.ee"
  override val wwwHost = "www.coo.ee"

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

  override val client: OkHttpClient = OkHttpClient.Builder().apply {
    eventListenerFactory(LoggingEventListener.Factory { s -> logger.info(s) })
    addInterceptor(RenewingInterceptor(credentialsStore, services))
    addNetworkInterceptor(AuthenticatingInterceptor(credentialsStore, services))
  }.build()

  companion object {
    val logger: Logger = LoggerFactory.getLogger(ProdAppServices::class.java)
  }
}
