package com.baulsupp.cooee.providers.providers

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.providers.BaseProvider

class ProvidersProvider : BaseProvider() {
  override val name = "providers"

  override suspend fun go(command: String, vararg args: String): GoResult {
    return when (command) {
      "add" -> {
        val name = args.first()

        appServices.providerConfigStore.store(user!!.email, name, mapOf())

        Completed("Added provider $name")
      }
      "remove" -> {
        val name = args.first()

        appServices.providerConfigStore.remove(user!!.email, name)

        Completed("Removed provider $name")
      }
      "list" -> {
        val names = appServices.providerConfigStore.forUser(user!!.email).map { it.providerName }

        Completed("Providers ${names.joinToString(", ")}")
      }
      else -> Unmatched
    }
  }

  override fun commandCompleter(): CommandCompleter {
    return SimpleCommandCompleter("add", "remove", "list")
  }
}
