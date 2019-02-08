package com.baulsupp.cooee.providers.strava

import com.baulsupp.cooee.BaseProviderTest
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

class StravaProviderTest: BaseProviderTest<StravaProvider>(StravaProvider::class) {
  @Test
  @Ignore("strava having issues")
  fun lastrun() = runBlocking {
    val result = p.go("strava", "lastrun")

    assertTrue(result is Completed)
    assertThat(result.message, startsWith("Distance: "))
  }

  @Test
  fun basicArgumentsCompletion() = runBlocking {
    assertThat(
      p.suggest("strava ").map { it.line },
      equalTo(listOf("strava lastrun"))
    )
  }

  override fun testAppServices(): TestAppServices {
    return appServices
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
