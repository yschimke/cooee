package com.baulsupp.cooee.providers.circleci

import com.baulsupp.cooee.BaseProviderTest
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.services.circleci.CircleCIAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
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
    val builds = p.readRecentBuilds()
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
