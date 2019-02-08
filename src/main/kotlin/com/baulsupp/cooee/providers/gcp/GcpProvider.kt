package com.baulsupp.cooee.providers.opsgenie

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.BaseProvider

class GcpProvider : BaseProvider() {
  override val name = "gcp"

  override suspend fun go(command: String, vararg args: String): GoResult = when {
    args.isEmpty() -> RedirectResult("https://cloud.google.com")
    else -> Unmatched
  }
}
