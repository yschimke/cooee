package com.baulsupp.cooee.providers.cooee

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.mongo.ProviderInstance
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.users.UserEntry
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CooeeProviderTest {
  val appServices = TestAppServices()
  val p = CooeeProvider()
  val userEntry = UserEntry("token", "Yuri Schimke", "yuri@coo.ee")

  fun login() {
    runBlocking { p.init(appServices, userEntry) }
  }

  @Test
  fun basic() {
    assertEquals("cooee", p.name)
  }

  @Test
  fun me() = runBlocking {
    login()

    val result = p.go("cooee", "me")

    assertTrue(result is Completed)
    assertThat(result.message, equalTo("Yuri Schimke"))
  }

  @Test
  fun home() = runBlocking {
    login()

    val result = p.go("cooee", "home")

    assertTrue(result is RedirectResult)
    assertThat(result.location, startsWith("https://www.coo.ee/?"))
  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("cooe").map { it.completion },
      equalTo(listOf("cooee"))
    )
  }

  @Test
  fun basicArgumentsCompletion() = runBlocking {
    assertThat(
      p.argumentCompleter().suggestArguments("cooee").map { it.completion },
      equalTo(listOf("me", "home", "auth", "renew"))
    )
  }
}
