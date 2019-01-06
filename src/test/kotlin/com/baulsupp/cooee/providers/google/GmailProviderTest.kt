package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.okurl.services.google.GoogleAuthInterceptor
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GmailProviderTest {
  val appServices = TestAppServices()
  val p = GmailProvider().apply { init(this@GmailProviderTest.appServices) }

  @Test
  fun basic() {
    assertEquals("gmail", p.name)
  }

  @Test
  fun inboxRedirect() = runBlocking {
    val result = p.go("gmail")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://mail.google.com/"))
  }

  @Test
  fun inbox() = runBlocking {
    p.setLocalCredentials(GoogleAuthInterceptor(), appServices)

    val result = p.go("gmail", listOf("label:inbox"))

    assertTrue(result is Completed)
    assertThat(result.message, CoreMatchers.startsWith("Results: "))
  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("gmai"),
      equalTo(listOf("gmail"))
    )
  }

  @Test
  fun basicArgumentsCompletion() = runBlocking {
    assertThat(
      p.argumentCompleter().suggestArguments("gmail"),
      equalTo(listOf("label:unread", "label:inbox"))
    )
  }
}
