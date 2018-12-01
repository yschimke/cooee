package com.baulsupp.cooee

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
  @Test
  fun testRoot() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertTrue(response.content?.contains("<title>cooee") ?: false)
      }
    }
  }

  @Test
  fun testGo() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/go?q=g abc").apply {
        assertEquals(HttpStatusCode.Found, response.status())
        assertEquals("https://google.com/?q=abc", response.headers["Location"])
      }
    }
  }

  @Test
  fun testGoInfo() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/api/v0/goinfo?q=g abc").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("{\"location\":\"https://google.com/?q=abc\"}", response.content)
      }
    }
  }

  @Test
  fun testJira() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/go?q=TRANS").apply {
        assertEquals(HttpStatusCode.Found, response.status())
        assertEquals("https://jira.atlassian.com/browse/TRANS", response.headers["Location"])
      }

      handleRequest(HttpMethod.Get, "/go?q=TRANS-2474").apply {
        assertEquals(HttpStatusCode.Found, response.status())
        assertEquals("https://jira.atlassian.com/browse/TRANS-2474", response.headers["Location"])
      }
    }
  }

  @Test
  fun testGitHub() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/go?q=yschimke/okurl").apply {
        assertEquals(HttpStatusCode.Found, response.status())
        assertEquals("https://github.com/yschimke/okurl", response.headers["Location"])
      }

      handleRequest(HttpMethod.Get, "/go?q=gh square/okhttp#4421").apply {
        assertEquals(HttpStatusCode.Found, response.status())
        assertEquals("https://github.com/square/okhttp/issues/4421", response.headers["Location"])
      }
    }
  }
}
