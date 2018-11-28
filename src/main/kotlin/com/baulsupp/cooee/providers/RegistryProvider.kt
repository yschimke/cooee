package com.baulsupp.cooee.providers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

object RegistryProvider : Provider {
  override suspend fun url(command: String, args: List<String>): RedirectResult = coroutineScope {
    provider(command)?.url(command, args) ?: RedirectResult.UNMATCHED
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

  val providers = listOf(GoogleProvider(), JiraProvider("https://jira.atlassian.com/"))
}
