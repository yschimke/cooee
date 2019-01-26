package com.baulsupp.cooee.providers.strava

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.startsWith
import org.junit.Assert.assertThat
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StravaProviderTest {
  val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = StravaProvider().apply {
    runBlocking { init(StravaProviderTest.appServices, userEntry) }
  }

  @Test
  fun basic() {
    assertEquals("strava", p.name)
  }

  @Test
  @Ignore("strava having issues")
  fun lastrun() = runBlocking {
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
      p.argumentCompleter().suggestArguments("strava"),
      equalTo(listOf("lastrun"))
    )
  }

  companion object {
    val appServices by lazy {
      TestAppServices().also {
        runBlocking {
          setLocalCredentials(StravaAuthInterceptor(), it)
        }
      }
    }
  }
}
