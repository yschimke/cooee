package com.baulsupp.cooee.providers.providers

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.providers.ProviderInstance

class ProvidersProvider : BaseProvider() {
  override val name = "providers"

  override suspend fun url(command: String, args: List<String>): GoResult {
    return when (command) {
        "add" -> {
          val name = args.first()

          // TODO validate known and one arg
          db!!.store(ProviderInstance(instance!!.user, name, mapOf()))

          Completed
        }
        "remove" -> {
          // TODO validate known and one arg
          val name = args.first()

          db!!.remove(instance!!.user, name)

          Completed
        }
        else -> Unmatched
    }
  }

  override fun commandCompleter(): CommandCompleter {
    return SimpleCommandCompleter("add", "remove")
  }
}
