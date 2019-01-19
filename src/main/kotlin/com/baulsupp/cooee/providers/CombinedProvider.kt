package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.Completion
import com.baulsupp.cooee.users.UserEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// TODO add exception handling for individual items and log errors
class CombinedProvider(val providers: List<Provider>) : ProviderFunctions {
  suspend fun init(appServices: AppServices, user: UserEntry?) {
    coroutineScope {
      providers.map { async { it.init(appServices, user) } }.awaitAll()
    }
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return object : ArgumentCompleter {
      override suspend fun suggestArguments(command: String, arguments: List<String>?): List<Completion> {
        return coroutineScope {
          providers.map { provider ->
            async {
              try {
                if (provider.matches(command)) {
                  provider.argumentCompleter().suggestArguments(command).map { it.copy(provider = provider.name) }
                } else {
                  listOf()
                }
              } catch (e: Exception) {
                log.warn("suggestArguments failed: " + provider.name, e)
                listOf<Completion>()
              }
            }
          }.awaitAll().flatten()
        }
      }
    }
  }

  override fun commandCompleter(): CommandCompleter {
    return object : CommandCompleter {
      override suspend fun suggestCommands(command: String): List<Completion> {
        return coroutineScope {
          providers.map { provider ->
            async {
              try {
                provider.commandCompleter().suggestCommands(command).filter { s -> s.startsWith(command) }
                  .map { it.copy(provider = provider.name) }
              } catch (e: Exception) {
                log.warn("suggestCommands failed: " + provider.name, e)
                listOf<Completion>()
              }
            }
          }.awaitAll().flatten()
        }
      }

      override suspend fun matches(command: String): Boolean {
        return coroutineScope {
          providers.map {
            async {
              try {
                it.commandCompleter().matches(command)
              } catch (e: Exception) {
                log.warn("matches failed: " + it.name, e)
                false
              }
            }
          }.awaitAll().any()
        }
      }
    }
  }

  override suspend fun go(command: String, vararg args: String): GoResult = coroutineScope {
    provider(command)?.go(command, *args) ?: Unmatched
  }

  private suspend fun CoroutineScope.provider(command: String): Provider? {
    return providers.map {
      async {
        try {
          if (it.commandCompleter().matches(command)) it else null
        } catch (e: Exception) {
          log.warn("provider search failed: " + it.name, e)
          null
        }
      }
    }.awaitAll().filterNotNull().firstOrNull()
  }

  override suspend fun matches(command: String): Boolean = coroutineScope {
    providers.map {
      async {
        try {
          it.matches(command)
        } catch (e: Exception) {
          log.warn("matches failed: " + it.name, e)
          false
        }
      }
    }
  }.awaitAll().any()

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java.declaringClass)
  }
}
