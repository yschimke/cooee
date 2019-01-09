package com.baulsupp.cooee.providers.strava

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.okurl.credentials.CredentialFactory
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import com.baulsupp.okurl.util.ClientException
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Assume.assumeFalse
import org.junit.Assume.assumeNotNull
import org.junit.AssumptionViolatedException
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
    p.setLocalCredentials(StravaAuthInterceptor(), appServices)

    val result = p.go("strava", "lastrun")

    assertTrue(result is Completed)
    assertThat(result.message, startsWith("Distance: "))
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
