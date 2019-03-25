package com.baulsupp.cooee.providers.circleci

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider

class CircleCiProvider : BaseProvider() {
  override val name = "circleci"

  override fun associatedServices(): Set<String> = setOf("circleci")

  override suspend fun go(command: String, vararg args: String): GoResult = when {
    args.isEmpty() -> RedirectResult("https://circleci.com/dashboard")
    else -> Unmatched
  }
}
