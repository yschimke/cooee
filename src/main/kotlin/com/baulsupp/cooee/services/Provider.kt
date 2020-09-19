package com.baulsupp.cooee.services

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.okurl.credentials.NoToken
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.credentials.Token
import com.baulsupp.okurl.credentials.TokenValue
import okhttp3.OkHttpClient

abstract class Provider(val name: String, val serviceDefinition: ServiceDefinition<*>? = null) : ProviderFunctions {
  lateinit var client: OkHttpClient
  lateinit var clientApi: ClientApi
  lateinit var cache: LocalCache
  lateinit var cachedToken: Token

  open suspend fun init(client: OkHttpClient, clientApi: ClientApi, cache: LocalCache) {
    this.client = client
    this.clientApi = clientApi
    this.cache = cache
  }

  // TODO cache
  suspend fun token(serviceDefinition: ServiceDefinition<*>): Token {
    val response = clientApi.tokenRequest(TokenRequest(service = serviceDefinition.shortName()))
    val tokenString = response.token?.token ?: return NoToken
    return TokenValue(serviceDefinition.parseCredentialsString(tokenString)!!)
  }

  suspend fun token(): Token {
    // TODO fix threading
    if (!this::cachedToken.isInitialized) {
      this.cachedToken = token(serviceDefinition!!)
    }

    return this.cachedToken
  }

  abstract suspend fun matches(command: String): Boolean
}
