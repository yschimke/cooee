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
// log multiple conflicts etc
class CombinedProvider(val providers: List<BaseProvider>) : BaseProvider() {
  override val name = "combined"

  override suspend fun suggest(command: String): List<Suggestion> {
    return CombinedSuggester(providers, check("caseinsensitive")).suggest(command)
  }

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    coroutineScope {
      super.init(appServices, user)

      forEachProvider {
        it.init(appServices, user)
      }
    }
  }

  override suspend fun go(command: String, vararg args: String): GoResult = coroutineScope {
    provider(command)?.go(command, *args) ?: Unmatched
  }

  private suspend fun provider(command: String): Provider? = coroutineScope {
    forEachProvider {
      if (it.matches(command)) it else null
    }.filterNotNull().firstOrNull()
  }

  override suspend fun matches(command: String): Boolean = coroutineScope {
    forEachProvider {
      it.matches(command)
    }.any()
  }

  private suspend inline fun <T> forEachProvider(
    crossinline fn: suspend (BaseProvider) -> T
  ): List<T?> = coroutineScope {
    providers.map {
      async {
        try {
          fn(it)
        } catch (e: Exception) {
          log.warn("provider failed: " + it.name, e)
          null
        }
      }
    }.awaitAll()
  }

  override suspend fun todo(): List<Suggestion> = coroutineScope {
    forEachProvider {
      it.todo()
    }
  }.flatMap { it.orEmpty() }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java.declaringClass)
  }
}
