package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.services.trello.TrelloAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrelloProviderTest {
  val appServices = TestAppServices()
  val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = TrelloProvider().apply {
    init(this@TrelloProviderTest.appServices, userEntry)
  }

  @Test
  fun basic() {
    assertEquals("trello", p.name)
  }

  @Test
  fun trelloHomepage() = runBlocking {
    p.setLocalCredentials(TrelloAuthInterceptor(), appServices)

    val result = p.go("trello")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://trello.com/"))
  }

  @Test
  fun trelloBoard() = runBlocking {
    p.setLocalCredentials(TrelloAuthInterceptor(), appServices)

    val result = p.go("cooee-dev")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://trello.com/b/7GPW8Zty/cooee-dev"))
  }

  @Test
  fun boards() = runBlocking {
    p.setLocalCredentials(TrelloAuthInterceptor(), appServices)

    val result = p.go("trello", "boards")

    assertTrue(result is Completed)
    assertThat(result.message, startsWith("Boards: "))
  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    p.setLocalCredentials(TrelloAuthInterceptor(), appServices)

    assertThat(
      p.commandCompleter().suggestCommands("trell"),
      equalTo(listOf("trello"))
    )
  }

  @Test
  fun boardsCommandCompletion() = runBlocking {
    p.setLocalCredentials(TrelloAuthInterceptor(), appServices)

    assertThat(
      p.commandCompleter().suggestCommands("cooee-d"),
      hasItem("cooee-dev")
    )
  }

  @Test
  fun basicArgumentsCompletion() = runBlocking {
    assertThat(
      p.argumentCompleter().suggestArguments("trello"),
      equalTo(listOf("boards"))
    )
  }
}
