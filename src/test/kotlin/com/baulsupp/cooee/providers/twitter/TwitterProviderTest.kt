package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.credentials.CredentialFactory
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.services.twitter.TwitterAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Assume.assumeNotNull
import org.junit.Test
import kotlin.test.assertEquals

class TwitterProviderTest {
  val appServices = TestAppServices()
  val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = TwitterProvider().apply {
    runBlocking { init(this@TwitterProviderTest.appServices, userEntry) }
  }

  @Test
  fun basic() {
    assertEquals("twitter", p.name)
  }

  @Test
  fun sendUser() = runBlocking {
    setLocalCredentials()

    assertEquals(
      RedirectResult("https://m.twitter.com/messages/compose?recipient_id=735627895645691905"),
      p.go("@shoutcooee")
    )
  }

  private suspend fun setLocalCredentials() {
    val serviceDefinition = TwitterAuthInterceptor().serviceDefinition
    val credentials = CredentialFactory.createCredentialsStore().get(
      serviceDefinition,
      DefaultToken
    )
    assumeNotNull(credentials)
    appServices.credentialsStore.set(serviceDefinition, "testuser", credentials!!)
  }

  @Test
  fun completeFriends() = runBlocking {
    setLocalCredentials()

    assertThat(
      p.commandCompleter().suggestCommands("@s").map { it.completion },
      hasItem("@shoutcooee")
    )
  }

  @Test
  fun completeFriendsPrefix() = runBlocking {
    setLocalCredentials()

    assertThat(
      p.commandCompleter().suggestCommands("").map { it.completion },
      hasItem("@s")
    )
  }
}
