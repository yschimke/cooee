package com.baulsupp.cooee.providers.cooee

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.okurl.authenticator.AuthInterceptor
import com.baulsupp.okurl.util.ClientException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import kotlin.reflect.full.createInstance

class AuthPair<T>(val credentials: T, val auth: AuthInterceptor<T>) {
  suspend fun validate(client: OkHttpClient): String {
    return try {
      val validated = auth.validate(client, credentials)
      "${auth.name()}\t${validated.username}"
    } catch (e: ClientException) {
      "${auth.name()}\t${e.message}"
    }
  }
}

class CooeeProvider : BaseProvider() {
  override val name = "cooee"

  override suspend fun go(command: String, vararg args: String): GoResult = when {
    args.firstOrNull() == "me" -> me()
    args.firstOrNull() == "auth" -> auth()
    args.isEmpty() || args.firstOrNull() == "home" -> RedirectResult(location = appServices.wwwUrl("/?token=" + user?.token))
    else -> Unmatched
  }

  private fun me(): Completed {
    return Completed(message = this.user?.name ?: "Anonymous")
  }

  private suspend fun auth(): Completed {
    val serviceNames = this.appServices.providerRegistry.registered.values.map { it.createInstance() }
      .flatMap { it.associatedServices() }.toSortedSet()

    val credentials = serviceNames.mapNotNull { findAuth(it) }

    return Completed(message = printCredentials(credentials))
  }

  private suspend fun printCredentials(credentials: List<AuthPair<*>>): String {
    return coroutineScope {
      credentials.map { service ->
        async {
          service.validate(client)
        }
      }.awaitAll().joinToString("\n")
    }
  }

  private suspend fun findAuth(name: String): AuthPair<*>? {
    val authInterceptor = appServices.services.find { it.name() == name }

    if (authInterceptor != null) {
      return authPair(authInterceptor)
    }

    return null
  }

  private suspend fun <T> authPair(authInterceptor: AuthInterceptor<T>): AuthPair<T>? {
    val credentials = appServices.credentialsStore.get(authInterceptor.serviceDefinition, user!!.email)

    return if (credentials != null) {
      AuthPair(credentials, authInterceptor)
    } else {
      null
    }
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return SimpleArgumentCompleter(listOf("me", "home", "auth"))
  }
}
