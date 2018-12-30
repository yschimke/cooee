package com.baulsupp.cooee

import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.okurl.credentials.CredentialsStore
import io.ktor.application.ApplicationCall

interface UserServices {
  fun credentialsStore(user: String): CredentialsStore
  suspend fun providersFor(call: ApplicationCall): RegistryProvider
}
