package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.services.google.GoogleAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GmailProviderTest {
  val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = GmailProvider().apply {
    runBlocking { init(GmailProviderTest.appServices, userEntry) }
  }

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
    val result = p.go("gmail", "label:inbox")

    assertTrue(result is Completed)
  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("gmai").map { it.line },
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

  companion object {
    val appServices by lazy {
      TestAppServices().also {
        runBlocking {
          setLocalCredentials(GoogleAuthInterceptor(), it)
        }
      }
    }
  }
}
