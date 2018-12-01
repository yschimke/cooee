package com.baulsupp.cooee.providers

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
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
  override suspend fun url(command: String, args: List<String>): GoResult = coroutineScope {
    provider(command)?.url(command, args) ?: Unmatched
  }

  override suspend fun targets(command: String, args: List<String>): List<Target> = coroutineScope {
    provider(command)?.targets(command, args).orEmpty()
  }

  private suspend fun CoroutineScope.provider(command: String): Provider? {
    return providers.map {
      async { if (it.matches(command)) it else null }
    }.awaitAll().filterNotNull().firstOrNull()
  }

  override suspend fun matches(command: String): Boolean = coroutineScope {
    providers.map {
      async { it.matches(command) }
    }
  }.awaitAll().any()

  val providers = listOf(
    GoogleProvider(), JiraProvider("https://jira.atlassian.com/", client),
    GithubProvider, TwitterProvider(client)
  )
}
