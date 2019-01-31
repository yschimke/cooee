package com.baulsupp.cooee.providers.trello

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.services.trello.TrelloAuthInterceptor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class TrelloProviderTest {
  private val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = TrelloProvider().apply {
    runBlocking {
      init(TrelloProviderTest.appServices, userEntry)
    }
  }

  @Test
  fun basic() {
    assertEquals("trello", p.name)
  }

  @Test
  fun trelloHomepage() = runBlocking {
    val result = p.go("trello")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://trello.com/"))
  }

  @Test
  fun trelloBoard() = runBlocking {
    val result = p.go("cooee-dev")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://trello.com/b/7GPW8Zty/cooee-dev"))
  }

  @Test
  fun boards() = runBlocking {
    val result = p.go("trello", "boards")

    assertTrue(result is Completed)
    assertThat(result.message, startsWith("Boards: "))
  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    assertThat(
      p.suggest("trell").map { it.line },
      equalTo(listOf("trello"))
    )
  }

  @Test
  fun boardsCommandCompletion() = runBlocking {
    assertThat(
      p.suggest("cooee-d").map { it.line },
      hasItem("cooee-dev")
    )
  }

  @Test
  fun basicArgumentsCompletion() = runBlocking {
    assertThat(
      p.suggest("trello ").map { it.line },
      equalTo(listOf())
    )
  }

  @Test
  fun matchesTrello() = runBlocking {
    assertTrue(p.matches("trello"))
  }

  @Test
  fun matchesCooeeDev() = runBlocking {
    assertTrue(p.matches("cooee-dev"))
  }

  companion object {
    val appServices by lazy {
      TestAppServices().also {
        runBlocking {
          setLocalCredentials(TrelloAuthInterceptor(), it)
        }
      }
    }
  }
}
