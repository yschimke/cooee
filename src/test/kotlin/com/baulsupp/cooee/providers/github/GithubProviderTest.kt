package com.baulsupp.cooee.providers.github

import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.services.github.GithubAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GithubProviderTest {
  private val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = GithubProvider().apply {
    runBlocking {
      init(GithubProviderTest.appServices, userEntry)
    }
  }

  @Test
  fun basic() {
    assertEquals("github", p.name)
    assertEquals(setOf("github"), p.associatedServices())
  }

  @Test
  fun fetchUserDetails() {
    assertNotNull(p.githubUser.name)
  }

  @Test
  fun completeUserRepos() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("yschimke/cooe").map { it.line },
      hasItem(equalTo("yschimke/cooee"))
    )
  }

  @Test
  fun completeUserRepos2() = runBlocking {
    assertThat(
      p.commandCompleter().suggestCommands("square/").map { it.line },
      hasItem(equalTo("square/okhttp"))
    )
  }

  @Test
  fun pullRequests() = runBlocking {
    assertThat(
      p.todo().map { it.line },
      hasItem(containsString("square/"))
    )
  }

  companion object {
    val appServices by lazy {
      TestAppServices().also {
        runBlocking {
          setLocalCredentials(GithubAuthInterceptor(), it)
        }
      }
    }
  }
}
