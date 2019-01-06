package com.baulsupp.cooee.test

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.providers.BaseProvider

class TestProvider: BaseProvider() {
  override val name = "test"

  override suspend fun go(command: String, args: List<String>): GoResult {
    return RedirectResult("https://test.com")
  }

  override fun commandCompleter(): CommandCompleter {
    return SimpleCommandCompleter("test")
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return SimpleArgumentCompleter("aaa", "bbb")
  }
}
