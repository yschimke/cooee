package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.services.atlassian.AtlassianAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JiraProviderTest {
  private val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val p = JiraProvider().apply {
    runBlocking {
      init(JiraProviderTest.appServices, userEntry)
    }
  }

  @Test
  fun basic() {
    assertEquals("jira", p.name)
  }

  @Test
  fun redirectProject() = runBlocking {
    val result = p.go("COOEE")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://shoutcooee.atlassian.net/browse/COOEE"))
  }

  @Test
  fun redirectIssue() = runBlocking {
    val result = p.go("COOEE-1")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://shoutcooee.atlassian.net/browse/COOEE-1"))
  }

  @Test
  fun completeCommandProjects() = runBlocking {
    assertThat(
      p.suggest("COOE").map { it.line },
      equalTo(listOf("COOEE-3", "COOEE-2", "COOEE-1", "COOEE"))
    )
  }

  @Test
  fun completeCommandIssues() = runBlocking {
    val suggestCommands = p.suggest("COOEE-")
    assertThat(
      suggestCommands.map { it.line },
      hasItem(equalTo("COOEE-1"))
    )
  }

  @Test
  fun completeArgumentsOnIssues() = runBlocking {
    val suggestArguments = p.suggest("COOEE-1 ").map(Suggestion::line)
    assertThat(
      suggestArguments,
      hasItem(equalTo("COOEE-1 comment"))
    )
  }

  @Test
  fun completeVoteCommand() = runBlocking {
    assertEquals(Completed("voted for COOEE-1"), p.go("COOEE-1", "vote"))
  }

  @Test
  fun completeCommentCommand() = runBlocking {
    assertEquals(Completed("comments on COOEE-1"), p.go("COOEE-1", "comment", "hello"))
  }

  companion object {
    val appServices by lazy {
      TestAppServices().also {
        runBlocking {
          setLocalCredentials(AtlassianAuthInterceptor(), it)
        }
      }
    }
  }
}
