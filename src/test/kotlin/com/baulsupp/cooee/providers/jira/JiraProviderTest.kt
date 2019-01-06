package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.okurl.services.atlassian.AtlassianAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JiraProviderTest {
  val appServices = TestAppServices()
  val p = JiraProvider().apply { init(this@JiraProviderTest.appServices) }

  @Test
  fun basic() {
    assertEquals("jira", p.name)
  }

  @Test
  fun redirectProject() = runBlocking {
    p.setLocalCredentials(AtlassianAuthInterceptor(), appServices)

    val result = p.go("COOEE")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://shoutcooee.atlassian.net/browse/COOEE"))
  }

  @Test
  fun redirectIssue() = runBlocking {
    p.setLocalCredentials(AtlassianAuthInterceptor(), appServices)

    val result = p.go("COOEE-1")

    assertTrue(result is RedirectResult)
    assertThat(result.location, equalTo("https://shoutcooee.atlassian.net/browse/COOEE-1"))
  }

  @Test
  fun completeCommandProjects() = runBlocking {
    p.setLocalCredentials(AtlassianAuthInterceptor(), appServices)

    assertThat(
      p.commandCompleter().suggestCommands("COOE"),
      equalTo(listOf("COOEE", "COOEE-"))
    )
  }

  @Test
  fun completeCommandIssues() = runBlocking {
    p.setLocalCredentials(AtlassianAuthInterceptor(), appServices)

    assertThat(
      p.commandCompleter().suggestCommands("COOEE-"),
      hasItem(equalTo("COOEE-1"))
    )
  }

  @Test
  fun completeArgumentsOnIssues() = runBlocking {
    p.setLocalCredentials(AtlassianAuthInterceptor(), appServices)

    assertThat(
      p.argumentCompleter().suggestArguments("COOEE-1"),
      hasItem(equalTo("comment"))
    )
  }

  @Test
  fun completeVoteCommand() = runBlocking {
    p.setLocalCredentials(AtlassianAuthInterceptor(), appServices)

    assertEquals(Completed("voted for COOEE-1"), p.go("COOEE-1", listOf("vote")))
  }

  @Test
  fun completeCommentCommand() = runBlocking {
    p.setLocalCredentials(AtlassianAuthInterceptor(), appServices)

    assertEquals(Completed("comments on COOEE-1"), p.go("COOEE-1", listOf("comment", "hello")))
  }
}
