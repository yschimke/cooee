package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.cooee.test.TestAppServices
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
  val p = TwitterProvider().apply { init(this@TwitterProviderTest.appServices) }

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
    p.configure(ProviderInstance("testuser", p.name, mapOf()))
  }

  @Test
  fun completeFriends() = runBlocking {
    setLocalCredentials()

    assertThat(
      p.commandCompleter().suggestCommands("@sh"),
      hasItem("@shoutcooee")
    )
  }
}
