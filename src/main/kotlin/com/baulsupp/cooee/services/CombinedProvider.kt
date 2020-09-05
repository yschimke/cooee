package com.baulsupp.cooee.services

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.*
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.supervisorScope
import okhttp3.OkHttpClient

open class CombinedProvider(vararg val providers: Provider): ProviderFunctions {
  suspend fun init(client: OkHttpClient, clientApi: ClientApi, cache: LocalCache) {
    supervisorScope {
      providers.map {
        async { it.init(client, clientApi, cache) }
      }
    }.joinAll()
  }

  override suspend fun runCommand(request: CommandRequest): CommandResponse? {
    val matches = supervisorScope {
      providers.map {
        Pair(it, async { it.matches(request.single_command) })
      }
    }

    for ((provider, matches) in matches) {
      try {
        if (matches.await()) {
          return provider.runCommand(request)
        }
      } catch (e: Exception) {
        // TODO log, and notify client
        System.err.println("TODO(${provider.name}): $e")
      }
    }

    return null
  }

  override suspend fun suggest(command: CompletionRequest): CompletionResponse? {
    return null
  }

  override suspend fun todo(): TodoResponse? {
    return null
  }
}