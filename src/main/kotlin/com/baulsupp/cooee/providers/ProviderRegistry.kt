package com.baulsupp.cooee.providers

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import java.io.InterruptedIOException

// TODO add exception handling for individual items and log errors
class RegistryProvider(val providers: List<Provider>) : ProviderFunctions {
  override fun argumentCompleter(): ArgumentCompleter {
    return object : ArgumentCompleter {
      override suspend fun suggestArguments(command: String, arguments: List<String>?): List<String>? {
        return coroutineScope {
          providers.map {
            async {
              try {
                it.argumentCompleter().suggestArguments(command).orEmpty()
              } catch (e: Exception) {
                log.warn("suggestArguments failed: " + it.name, e)
                listOf<String>()
              }
            }
          }.awaitAll().flatten()
        }
      }
    }
  }

  override fun commandCompleter(): CommandCompleter {
    return object : CommandCompleter {
      override suspend fun suggestCommands(command: String): List<String> {
        return coroutineScope {
          providers.map {
            async {
              try {
                it.commandCompleter().suggestCommands(command)
              } catch (e: Exception) {
                log.warn("suggestCommands failed: " + it.name, e)
                listOf<String>()
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

  override suspend fun go(command: String, args: List<String>): GoResult = coroutineScope {
    provider(command)?.go(command, args) ?: Unmatched
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
    val log = LoggerFactory.getLogger(this::class.java.declaringClass)
  }
}
