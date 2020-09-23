package com.baulsupp.cooee.services

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.CompletionSuggestion
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.LogSeverity
import com.baulsupp.cooee.p.error
import com.baulsupp.cooee.p.single_command
import com.baulsupp.cooee.p.warn
import com.baulsupp.okurl.util.ClientException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.supervisorScope
import okhttp3.OkHttpClient

open class CombinedProvider(vararg val providers: Provider) : ProviderFunctions {
  private lateinit var clientApi: ClientApi

  suspend fun init(client: OkHttpClient, clientApi: ClientApi, cache: LocalCache) {
    this.clientApi = clientApi

    supervisorScope {
      providers.map {
        async { it.init(client, clientApi, cache) }
      }
    }.joinAll()
  }

  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    val command = request.single_command ?: return null

    val provider = findByMatchingCommand(command) ?: return null

    try {
      return provider.runCommand(request)
    } catch (ce: ClientException) {
      if (ce.code == 401) {
        clientApi.logToClient(LogRequest.warn("unauthorized"))
        return flowOf(CommandResponse.error("unauthorized"))
      }
      return flowOf(CommandResponse.error(message = ce.responseMessage))
    }
  }

  private suspend fun findByMatchingCommand(command: String): Provider? {
    val matches = supervisorScope {
      providers.map {
        Pair(it, async { it.matches(command) })
      }
    }

    for ((provider, matches) in matches) {
      try {
        if (matches.await()) {
          return provider
        }
      } catch (e: Exception) {
        val log: LogRequest = LogRequest(message = e.toString(), severity = LogSeverity.WARN)
        clientApi.logToClient(log)
        System.err.println("TODO(${provider.name}): $e")
      }
    }
    return null
  }

  private suspend fun findAllSuggestions(request: CompletionRequest): List<CompletionSuggestion> {
    val matches = supervisorScope {
      providers.map {
        Pair(it, async { it.suggest(request) })
      }
    }

    return matches.flatMap { (provider, suggestions) ->
      try {
        suggestions.await()
      } catch (e: Exception) {
        val log: LogRequest = LogRequest(message = e.toString(), severity = LogSeverity.WARN)
        clientApi.logToClient(log)
        System.err.println("TODO(${provider.name}): $e")
        listOf()
      }
    }
  }

  override suspend fun suggest(request: CompletionRequest): List<CompletionSuggestion> {
    val line = request.line

    if (line.contains("\\s".toRegex())) {
      val command = line.split("\\s+".toRegex()).first()
      val provider = findByMatchingCommand(command) ?: return listOf()
      return provider.suggest(request)
    } else {
      return findAllSuggestions(request)
    }
  }
}