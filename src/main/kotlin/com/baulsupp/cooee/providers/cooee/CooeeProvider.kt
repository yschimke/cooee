package com.baulsupp.cooee.providers.cooee

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider

class CooeeProvider : BaseProvider() {
  override val name = "cooee"

  override suspend fun go(command: String, vararg args: String): GoResult = when {
    args.firstOrNull() == "me" -> me()
    args.isEmpty() || args.firstOrNull() == "home" -> RedirectResult(location = appServices.wwwUrl("/?token=ABC"))
    else -> Unmatched
  }

  private fun me(): Completed {
    return Completed(message = this.user?.name ?: "Anonymous")
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return SimpleArgumentCompleter(listOf("me", "home"))
  }
}
