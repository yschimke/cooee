package com.baulsupp.cooee

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
  @Test
  fun testRoot() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("Cooee!", response.content)
      }
    }
  }

  @Test
  fun testGo() {
    withTestApplication({ module(testing = true) }) {
      handleRequest(HttpMethod.Get, "/go?q=abc").apply {
        assertEquals(HttpStatusCode.Found, response.status())
        assertEquals("https://google.com?q=abc", response.headers["Location"])
      }
    }
  }
}
