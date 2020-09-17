package com.baulsupp.cooee.services

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.okurl.credentials.NoToken
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.credentials.Token
import com.baulsupp.okurl.credentials.TokenValue
import okhttp3.OkHttpClient

abstract class Provider(val name: String) : ProviderFunctions {
  lateinit var client: OkHttpClient
  lateinit var clientApi: ClientApi
  lateinit var cache: LocalCache
  lateinit var token: Token

  open suspend fun init(client: OkHttpClient, clientApi: ClientApi, cache: LocalCache) {
    this.client = client
    this.clientApi = clientApi
    this.cache = cache
  }

  // TODO cache
  suspend fun token(serviceDefinition: ServiceDefinition<*>): Token {
    val tokenString = clientApi.tokenRequest(name).token ?: return NoToken
    return TokenValue(serviceDefinition.parseCredentialsString(tokenString)!!)
  }

  abstract suspend fun matches(command: String): Boolean
}
