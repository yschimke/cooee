package com.baulsupp.cooee.test

import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.okurl.authenticator.AuthInterceptor
import com.baulsupp.okurl.credentials.CredentialFactory
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.util.ClientException
import org.junit.Assume
import org.junit.AssumptionViolatedException


suspend fun <T> BaseProvider.setLocalCredentials(authInterceptor: AuthInterceptor<T>, appServices: TestAppServices) {
  val serviceDefinition = authInterceptor.serviceDefinition
  val credentials = CredentialFactory.createCredentialsStore().get(
    serviceDefinition,
    DefaultToken
  )
  Assume.assumeNotNull(credentials)
  try {
    authInterceptor.validate(appServices.client, credentials!!)
  } catch (e: ClientException) {
    throw AssumptionViolatedException("needs working credentials", e)
  }
  appServices.credentialsStore.set(serviceDefinition, "testuser", credentials)
  this.configure(ProviderInstance("testuser", this.name, mapOf()))
}
