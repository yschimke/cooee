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

data class AuthPair<T>(val credentials: T, val auth: AuthInterceptor<T>) {
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
    args.firstOrNull() == "renew" -> renew()
    args.isEmpty() || args.firstOrNull() == "home" -> RedirectResult(location = appServices.wwwUrl("/?token=" + user?.token))
    else -> Unmatched
  }

  private fun me(): Completed {
    return Completed(message = this.user?.name ?: "Anonymous")
  }

  private suspend fun auth(services: List<String>? = null) =
    Completed(message = printCredentials(findServiceAuths(services)))

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

  private suspend fun renew(services: List<String>? = null): Completed {
    val credentials = findServiceAuths(services)

    val renewed = credentials.mapNotNull { renew(it) }

    return Completed(message = printCredentials(renewed))
  }

  private suspend fun <T> renew(auth: AuthPair<T>): AuthPair<T>? {
    val newToken = auth.auth.renew(appServices.client, auth.credentials)

    if (newToken != null && newToken != auth.credentials) {
      appServices.credentialsStore.set(auth.auth.serviceDefinition, user!!.email, newToken)
      return auth.copy(credentials = newToken)
    }

    return null
  }

  private suspend fun findServiceAuths(services: List<String>?): List<AuthPair<*>> =
    (services ?: allServiceNames()).mapNotNull { findAuth(it) }

  private fun allServiceNames() = this.appServices.providerRegistry.registered.values.map { it.createInstance() }
    .flatMap { it.associatedServices() }.toSortedSet()

  private suspend fun <T> authPair(authInterceptor: AuthInterceptor<T>): AuthPair<T>? {
    val credentials = appServices.credentialsStore.get(authInterceptor.serviceDefinition, user!!.email)

    return if (credentials != null) {
      AuthPair(credentials, authInterceptor)
    } else {
      null
    }
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return SimpleArgumentCompleter(listOf("me", "home", "auth", "renew"))
  }
}
