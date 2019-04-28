package com.baulsupp.cooee.providers.circleci

import com.baulsupp.cooee.BaseProviderTest
import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.services.circleci.CircleCIAuthInterceptor
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.startsWith
import org.junit.Assert.assertThat
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CircleCiProviderTest: BaseProviderTest<CircleCiProvider>(CircleCiProvider::class) {
  @Test
  fun homepage() = runBlocking {
    val result = p.go("circleci")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://circleci.com/dashboard"))
  }

//  @Test
//  fun okhttp() = runBlocking {
//    val result = p.go("circleci", "square/okhttp")
//
//    assertTrue(result is RedirectResult)
//    assertThat(result.location, equalTo(""))
//  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    assertThat(
      p.suggest("circlec").map { it.line },
      equalTo(listOf("circleci"))
    )
  }

  @Test
  fun fetchBuilds() = runBlocking {
    val builds = appServices.client.queryList<Build>("https://circleci.com/api/v1.1/recent-builds?limit=10&shallow=true")
    println(builds)
  }

//  @Test
//  fun basicArgumentsCompletion() = runBlocking {
//    assertThat(
//      p.suggest("circleci s").map { it.line },
//      equalTo(listOf("strava lastrun"))
//    )
//  }

  override fun testAppServices(): TestAppServices {
    return appServices
  }

  companion object {
    val appServices by lazy {
      TestAppServices().also {
        runBlocking {
          setLocalCredentials(CircleCIAuthInterceptor(), it)
        }
      }
    }
  }
}
