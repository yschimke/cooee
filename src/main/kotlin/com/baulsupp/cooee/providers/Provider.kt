package com.baulsupp.cooee.providers

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter

interface Provider: ProviderFunctions {
  val name: String
}
