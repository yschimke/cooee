package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.services.twitter.TwitterAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals

class TwitterProviderTest {
  val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = TwitterProvider().apply {
    runBlocking { init(TwitterProviderTest.appServices, userEntry) }
  }

  @Test
  fun basic() {
    assertEquals("twitter", p.name)
  }

  @Test
  fun sendUser() = runBlocking {
    assertEquals(
      RedirectResult("https://m.twitter.com/messages/compose?recipient_id=735627895645691905"),
      p.go("@shoutcooee")
    )
  }

  @Test
  fun completeFriends() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("@s").map { it.line },
      hasItem("@shoutcooee")
    )
  }

  @Test
  fun completeFriendsPrefix() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("").map { it.line },
      equalTo(listOf())
    )
  }

  companion object {
    val appServices by lazy {
      TestAppServices().also {
        runBlocking {
          setLocalCredentials(TwitterAuthInterceptor(), it)
        }
      }
    }
  }
}
