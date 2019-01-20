package com.baulsupp.cooee.test

import com.baulsupp.okurl.authenticator.AuthInterceptor
import com.baulsupp.okurl.credentials.CredentialFactory
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.util.ClientException
import org.junit.Assume
import org.junit.AssumptionViolatedException

suspend fun <T> setLocalCredentials(
  authInterceptor: AuthInterceptor<T>,
  appServices: TestAppServices
) {
  val serviceDefinition = authInterceptor.serviceDefinition
  val credentialsStore = CredentialFactory.createCredentialsStore()
  val credentials = credentialsStore.get(
    serviceDefinition,
    DefaultToken
  )
  Assume.assumeNotNull(credentials)
  try {
    authInterceptor.validate(appServices.client, credentials!!)
    appServices.credentialsStore.set(serviceDefinition, "testuser", credentials)
  } catch (e: ClientException) {
    var newCredentials: T? = null
    if (authInterceptor.canRenew(credentials!!)) {
      newCredentials = authInterceptor.renew(appServices.client, credentials)
    }

    if (newCredentials != null && newCredentials != credentials) {
      credentialsStore.set(serviceDefinition, DefaultToken.name, newCredentials)
      appServices.credentialsStore.set(serviceDefinition, "testuser", newCredentials)
    } else {
      throw AssumptionViolatedException("needs working credentials", e)
    }
  }
}
