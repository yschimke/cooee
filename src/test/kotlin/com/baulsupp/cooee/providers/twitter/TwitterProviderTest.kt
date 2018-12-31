package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.kotlin.OkShell
import com.baulsupp.okurl.services.twitter.TwitterServiceDefinition
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Assume.assumeTrue
import org.junit.Test
import kotlin.test.assertEquals

class TwitterProviderTest {
  val appServices = TestAppServices()
  val p = TwitterProvider(appServices.client)

  @Test
  fun basic() {
    assertEquals("twitter", p.name)
  }

  @Test
  fun sendUser() = runBlocking {
    assumeHasCredentialsSet()

    assertEquals(
      RedirectResult("https://m.twitter.com/messages/compose?recipient_id=735627895645691905"),
      p.go("@shoutcooee")
    )
  }

  private suspend fun assumeHasCredentialsSet() {
    assumeTrue(
      OkShell.instance?.commandLine?.credentialsStore?.get(
        TwitterServiceDefinition(),
        DefaultToken
      ) != null ?: false
    )
  }

  @Test
  fun completeFriends() = runBlocking {
    assumeHasCredentialsSet()

    assertThat(
      p.commandCompleter().suggestCommands("@sh"),
      hasItem("@shoutcooee")
    )
  }
}
