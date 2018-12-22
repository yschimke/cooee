package com.baulsupp.cooee

import com.baulsupp.okurl.kotlin.mapAdapter
import com.baulsupp.okurl.kotlin.moshi
import io.jsonwebtoken.Jwt
import io.jsonwebtoken.impl.DefaultJwtBuilder
import io.ktor.http.HttpMethod
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Found
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.header
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApplicationTest {
  @Test
  fun testRoot() {
    testRequest("/").apply {
      assertTrue(response.content?.contains("<title>cooee") ?: false)
    }
  }

  @Test
  fun testGoCommandWithSpaces() {
    testRequest("/go?q=g abc", expectedCode = Found).apply {
      assertEquals("https://www.google.com/search?q=abc", response.headers["Location"])
    }
  }

  @Test
  fun testGoCommandWithPlus() {
    testRequest("/go?q=g+yriu", expectedCode = Found).apply {
      assertEquals("https://www.google.com/search?q=yriu", response.headers["Location"])
    }
  }

  @Test
  fun testGoInfo() {
    testRequest("/api/v0/goinfo?q=g abc").apply {
      assertEquals("{\"location\":\"https://www.google.com/search?q=abc\"}", response.content)
    }
  }

  @Test
  fun testCors() {
    withTestApplication({ test() }) {
      handleRequest(HttpMethod.Get, "/api/v0/goinfo?q=g abc") {
        addHeader("Origin", "https://google.com")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("{\"location\":\"https://www.google.com/search?q=abc\"}", response.content)
        assertEquals("*", response.headers["Access-Control-Allow-Origin"])
      }
    }
  }

  @Test
  fun testCommandCompletion() {
    testRequest("/api/v0/command-completion?q=g").apply {
      assertEquals("{\"completions\":[\"g\",\"gl\",\"google\"]}", response.content)
    }
  }

  @Test
  fun testJiraCommandCompletionDefault() {
    testCompletion("") {
      // no default project completion for now
      assertFalse { it.contains("TRANS") }
    }
  }

  @Test
  fun testJiraCommandCompletionPartialProject() {
    testCompletion("T") {
      assertEquals(listOf("TRANS", "TRANS-"), it)
    }
  }

  @Test
  fun testJiraCommandCompletionProject() {
    testCompletion("TRANS") {
      assertEquals(listOf("TRANS", "TRANS-"), it)
    }
  }

  @Test
  fun testJiraCommandCompletionProjectIssueStart() {
    testCompletion("TRANS-") {
      assertEquals(listOf("TRANS-123", "TRANS-1234", "TRANS-1235"), it)
    }
  }

  @Test
  fun testJiraCommandCompletionProjectIssue() {
    testCompletion("TRANS-123") { completions ->
      assertEquals(listOf("TRANS-123") + (1230..1239).map { "TRANS-$it" }, completions)
    }
  }

  @Test
  fun testJiraCommandCompletionProjectIssueLong() {
    testCompletion("TRANS-12345") {
      assertEquals(listOf("TRANS-12345"), it)
    }
  }

  private fun testCompletion(prefix: String, check: (List<String>) -> Unit = {}) {
    withTestApplication({ test() }) {
      handleRequest(HttpMethod.Get, "/api/v0/command-completion?q=$prefix").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        val completions = moshi.mapAdapter<List<String>>().fromJson(response.content!!)

        check(completions!!.getValue("completions"))
      }
    }
  }

  @Test
  fun testArgumentCompletion() {
    testRequest("/api/v0/argument-completion?q=TRANS-1234 ").apply {
      assertEquals(HttpStatusCode.OK, response.status())
      assertEquals("{\"completions\":[\"close\",\"comment\"]}", response.content)
    }
  }

  @Test
  fun testJiraProject() {
    testRequest("/go?q=TRANS", expectedCode = Found).apply {
      assertEquals("https://jira.atlassian.com/browse/TRANS", response.headers["Location"])
    }
  }

  @Test
  fun testJiraIssue() {
    testRequest("/go?q=TRANS-2474", expectedCode = Found).apply {
      assertEquals("https://jira.atlassian.com/browse/TRANS-2474", response.headers["Location"])
    }
  }

  @Test
  fun testGitHubProject() {
    testRequest("/go?q=yschimke/okurl", expectedCode = Found).apply {
      assertEquals("https://github.com/yschimke/okurl", response.headers["Location"])
    }
  }

  @Test
  fun testGitHubIssue() {
    testRequest("/go?q=square/okhttp#4421", expectedCode = Found).apply {
      assertEquals("https://github.com/square/okhttp/issues/4421", response.headers["Location"])
    }
  }

  @Test
  fun testBookmarkCompletion() {
    testCompletion("m") {
      assertEquals(listOf("man"), it)
    }
  }

  @Test
  fun testManBookmark() {
    testRequest("/go?q=man", expectedCode = Found).apply {
      assertEquals(
        "https://man.com",
        response.headers["Location"]
      )
    }
  }

  @Test
  fun testUser() {
    testRequest("/api/v0/user", user = "yuri").apply {
      assertEquals("{\"email\":\"yuri@coo.ee\",\"token\":\"yuri\",\"user\":\"yuri\"}", response.content)
    }
  }

  @Test
  fun testLogin() {
    testRequest("/login?user=yuri&callback=http://localhost:3000/callback", expectedCode = Found).apply {
      assertEquals(
        "http://localhost:3000/callback?code=eyJhbGciOiJub25lIn0.eyJ1c2VyIjoieXVyaSJ9.",
        response.headers["Location"]
      )
    }
  }

  private fun testRequest(
    path: String,
    method: HttpMethod = Get,
    user: String? = null,
    expectedCode: HttpStatusCode = OK,
    fn: TestApplicationCall.() -> Unit = {}
  ) = withTestApplication({ test() }) {
    handleRequest(method, path) {
      if (user != null) {
        val token = DefaultJwtBuilder().claim("user", user).compact()
        addHeader("Authorization", "Bearer $token")
      }
    }.apply {
      assertEquals(expectedCode, response.status())
      fn(this)
    }
  }
}
