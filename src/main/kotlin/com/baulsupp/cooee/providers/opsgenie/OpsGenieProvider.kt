package com.baulsupp.cooee.providers.opsgenie

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider

class OpsGenieProvider : BaseProvider() {
  override val name = "opsgenie"

  override fun associatedServices(): Set<String> = setOf("opsgenie")

  override suspend fun go(command: String, vararg args: String): GoResult = when {
    args.isEmpty() -> RedirectResult("https://app.opsgenie.com")
    else -> Unmatched
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return SimpleArgumentCompleter(listOf("lastrun"))
  }
}
