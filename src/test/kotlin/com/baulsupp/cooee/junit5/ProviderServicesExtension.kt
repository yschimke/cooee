package com.baulsupp.cooee.junit5

import com.apollographql.apollo.ApolloClient
import com.baulsupp.cooee.CooeeApplication
import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.TokenResponse
import com.baulsupp.cooee.p.TokenUpdate
import com.baulsupp.cooee.services.Provider
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Token
import com.baulsupp.okurl.credentials.CredentialsStore
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.credentials.SimpleCredentialsStore
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.fail

class ProviderServicesExtension : ParameterResolver {
  val localCache = LocalCache()
  val app = CooeeApplication()
  val okHttpClient = app.client()
  val client = app.githubApolloClient(okHttpClient)
  val credentialsStore = SimpleCredentialsStore

  override fun supportsParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): Boolean {
    return parameterContext.parameter.type in Supported
  }

  override fun resolveParameter(
    parameterContext: ParameterContext,
    extensionContext: ExtensionContext
  ): Any {
    return when (parameterContext.parameter.type) {
      LocalCache::class.java -> localCache
      CooeeApplication::class.java -> app
      OkHttpClient::class.java -> okHttpClient
      ApolloClient::class.java -> client
      CredentialsStore::class.java -> credentialsStore
      else -> fail("unknown param " + parameterContext.parameter.type)
    }
  }

  companion object {
    val Supported =
        listOf(LocalCache::class.java, CooeeApplication::class.java, OkHttpClient::class.java,
            ApolloClient::class.java, CredentialsStore::class.java)
  }
}