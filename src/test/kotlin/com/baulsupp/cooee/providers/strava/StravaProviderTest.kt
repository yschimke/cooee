package com.baulsupp.cooee.providers.strava

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.okurl.credentials.CredentialFactory
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Assume.assumeNotNull
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StravaProviderTest {
  val appServices = TestAppServices()
  val p = StravaProvider().apply { init(this@StravaProviderTest.appServices) }

  @Test
  fun basic() {
    assertEquals("strava", p.name)
  }

  @Test
  fun lastrun() = runBlocking {
    setLocalCredentials()

    val result = p.go("strava", listOf("lastrun"))

    assertTrue(result is Completed)
    assertThat(result.message, CoreMatchers.startsWith("Distance: "))
  }

  private suspend fun setLocalCredentials() {
    val serviceDefinition = StravaAuthInterceptor().serviceDefinition
    val credentials = CredentialFactory.createCredentialsStore().get(
      serviceDefinition,
      DefaultToken
    )
    assumeNotNull(credentials)
    appServices.credentialsStore.set(serviceDefinition, "testuser", credentials!!)
    p.configure(ProviderInstance("testuser", p.name, mapOf()))
  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("strav"),
      equalTo(listOf("strava"))
    )
  }

  @Test
  fun basicArgumentsCompletion() = runBlocking {
    assertThat(
      p.argumentCompleter().suggestArguments("strava"),
      equalTo(listOf("lastrun"))
    )
  }
}
