package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.suggester.CombinedSuggester
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.users.UserEntry
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// TODO add exception handling for individual items and log errors
class CombinedProvider(val providers: List<BaseProvider>) : ProviderFunctions {
  override suspend fun suggest(command: String): List<Suggestion> {
    return CombinedSuggester(providers).suggest(command)
  }

  suspend fun init(appServices: AppServices, user: UserEntry?) {
    coroutineScope {
      providers.map {
        async {
          it.init(appServices, user)
        }
      }.awaitAll()
    }
  }

  override suspend fun go(command: String, vararg args: String): GoResult = coroutineScope {
    provider(command)?.go(command, *args) ?: Unmatched
  }

  private suspend fun provider(command: String): Provider? = coroutineScope {
    // TODO log multiple results
    providers.map {
      async {
        try {
          if (it.matches(command)) it else null
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
