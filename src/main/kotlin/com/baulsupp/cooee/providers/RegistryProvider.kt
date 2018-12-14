package com.baulsupp.cooee.providers

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.providers.github.GithubProvider
import com.baulsupp.cooee.providers.google.GoogleProvider
import com.baulsupp.cooee.providers.jira.JiraProvider
import com.baulsupp.cooee.providers.twitter.TwitterProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient

class RegistryProvider(client: OkHttpClient) : Provider {
  override fun argumentCompleter(): ArgumentCompleter {
    return object : ArgumentCompleter {
      override suspend fun suggestArguments(command: String): List<String>? {
        return coroutineScope {
          providers.map {
            async {
              it.argumentCompleter().suggestArguments(command).orEmpty()
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
              it.commandCompleter().suggestCommands(command)
            }
          }.awaitAll().flatten()
        }
      }

      override suspend fun matches(command: String): Boolean {
        return coroutineScope {
          providers.map {
            async {
              it.commandCompleter().matches(command)
            }
          }.awaitAll().any()
        }
      }
    }
  }

  override suspend fun url(command: String, args: List<String>): GoResult = coroutineScope {
    provider(command)?.url(command, args) ?: Unmatched
  }

  private suspend fun CoroutineScope.provider(command: String): Provider? {
    return providers.map {
      async { if (it.commandCompleter().matches(command)) it else null }
    }.awaitAll().filterNotNull().firstOrNull()
  }

  override suspend fun matches(command: String): Boolean = coroutineScope {
    providers.map {
      async { it.matches(command) }
    }
  }.awaitAll().any()

  val providers = listOf(
    GoogleProvider(), JiraProvider("https://jira.atlassian.com/", client),
    GithubProvider, TwitterProvider(client), BookmarksProvider()
  )
}
