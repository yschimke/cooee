package com.baulsupp.cooee.providers

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter

interface ProviderFunctions {
  suspend fun go(command: String, vararg args: String): GoResult

  suspend fun matches(command: String): Boolean = commandCompleter().matches(command)

  fun argumentCompleter(): ArgumentCompleter = SimpleArgumentCompleter(null)

  fun commandCompleter(): CommandCompleter
}
