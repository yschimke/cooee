package com.baulsupp.cooee.providers.jira

import okhttp3.OkHttpClient
import org.junit.Test
import kotlin.test.assertEquals

class JiraProviderTest {
  val client = OkHttpClient()
  val p = JiraProvider("https://jira.atlassian.com/", client)

  @Test
  fun basic() {
    assertEquals("jira", p.name)
    assertEquals("https://jira.atlassian.com/", p.url)
  }
}
