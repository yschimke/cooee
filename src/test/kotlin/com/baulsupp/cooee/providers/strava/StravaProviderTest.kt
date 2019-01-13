package com.baulsupp.cooee.providers.strava

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StravaProviderTest {
  val appServices = TestAppServices()
  val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = StravaProvider().apply {
    init(this@StravaProviderTest.appServices, userEntry)
  }

  @Test
  fun basic() {
    assertEquals("strava", p.name)
  }

  @Test
  @Ignore("strava having issues")
  fun lastrun() = runBlocking {
    p.setLocalCredentials(StravaAuthInterceptor(), appServices)

    val result = p.go("strava", "lastrun")

    assertTrue(result is Completed)
    assertThat(result.message, startsWith("Distance: "))
  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("strav").map { it.completion },
      equalTo(listOf("strava"))
    )
  }

  @Test
  fun basicArgumentsCompletion() = runBlocking {
    assertThat(
      p.argumentCompleter().suggestArguments("strava").map { it.completion },
      equalTo(listOf("lastrun"))
    )
  }
}
