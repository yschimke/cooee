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
}
