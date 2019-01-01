package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.test.TestAppServices
import okhttp3.OkHttpClient
import org.junit.Test
import kotlin.test.assertEquals

class JiraProviderTest {
  val appServices = TestAppServices()
  val p = JiraProvider("https://jira.atlassian.com/").apply { init(this@JiraProviderTest.appServices) }

  @Test
  fun basic() {
    assertEquals("jira", p.name)
    assertEquals("https://jira.atlassian.com/", p.url)
  }
}
