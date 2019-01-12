package com.baulsupp.cooee

import com.baulsupp.cooee.api.CompletionItem
import com.baulsupp.cooee.api.Completions
import com.baulsupp.cooee.mongo.ProviderInstance
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.okurl.kotlin.moshi
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Found
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.runBlocking
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
  val services = TestAppServices()

  fun Application.test() = module(services, false)

  @Test
  fun testRoot() {
    testRequest("/") {
      assertTrue(response.content?.contains("<title>cooee") ?: false)
    }
  }

  @Test
  fun testGoCommandWithSpaces() {
    testRequest("/go?q=g abc", expectedCode = Found) {
      assertEquals("https://www.google.com/search?q=abc", response.headers["Location"])
    }
  }

  @Test
  fun testGoCommandWithPlus() {
    testRequest("/go?q=g+yriu", expectedCode = Found) {
      assertEquals("https://www.google.com/search?q=yriu", response.headers["Location"])
    }
  }

  @Test
  fun testGoInfo() {
    testRequest("/api/v0/goinfo?q=g abc") {
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
    runBlocking {
      services.providerConfigStore.store("yuri@coo.ee", "google", mapOf())
      services.providerConfigStore.store("yuri@coo.ee", "bookmarks", mapOf("bookmarks" to BookmarksProvider.defaultBookmarks))
    }

    testRequest("/api/v0/completion?q=g", user = "yuri") {
      assertEquals(
        "{\"completions\":[" +
          "{\"word\":\"g\",\"line\":\"g\",\"description\":\"Command for 'g'\"}," +
          "{\"word\":\"gl\",\"line\":\"gl\",\"description\":\"Command for 'gl'\"}," +
          "{\"word\":\"google\",\"line\":\"google\",\"description\":\"Command for 'google'\"}," +
          "{\"word\":\"gmail\",\"line\":\"gmail\",\"description\":\"Command for 'gmail'\"}]}", response.content
      )
    }
  }

  private fun testCompletion(prefix: String, check: (Completions) -> Unit = {}) {
    withTestApplication({ test() }) {
      handleRequest(HttpMethod.Get, "/api/v0/completion?q=$prefix").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        val completions: Completions = moshi.adapter(Completions::class.java).fromJson(response.content!!)!!

        check(completions)
      }
    }
  }

  @Test
  fun testArgumentCompletion() {
    runBlocking {
      services.providerConfigStore.store("yuri@coo.ee", "test", mapOf())
    }

    testRequest("/api/v0/completion?q=test ", user = "yuri") {
      assertEquals(HttpStatusCode.OK, response.status())
      assertEquals(
        "{\"completions\":[{\"word\":\"test\",\"line\":\"test\",\"description\":\"Command for 'test'\"}]}",
        response.content
      )
    }
  }

  @Test
  fun testGitHubProject() {
    testRequest("/go?q=yschimke/okurl", expectedCode = Found) {
      assertEquals("https://github.com/yschimke/okurl", response.headers["Location"])
    }
  }

  @Test
  fun testGitHubIssue() {
    testRequest("/go?q=square/okhttp#4421", expectedCode = Found) {
      assertEquals("https://github.com/square/okhttp/issues/4421", response.headers["Location"])
    }
  }

  @KtorExperimentalLocationsAPI
  @Test
  fun testBookmarkCompletion() {
    testCompletion("gm") {
      assertEquals(Completions(listOf(CompletionItem("gmail", "gmail", "Command for 'gmail'"))), it)
    }
  }

  @Test
  fun testGmailBookmark() {
    testRequest("/go?q=gmail", expectedCode = Found) {
      assertEquals(
        "https://mail.google.com",
        response.headers["Location"]
      )
    }
  }

  @Test
  fun testUser() {
    val token = services.userAuthenticator.tokenFor("yuri")

    testRequest("/api/v0/user", user = "yuri") {
      assertEquals("{\"token\":\"$token\",\"name\":\"yuri\",\"email\":\"yuri@coo.ee\"}", response.content)
    }
  }

  @Test
  fun testAddBookmarkProvider() {
    testRequest("/go?q=add test", user = "yuri")

    assertEquals(1, services.providerConfigStore.providerInstances.size)
  }

  @Test
  fun testAddBookmarkName() {
    runBlocking {
      services.providerConfigStore.store("yuri@coo.ee", "bookmarks", mapOf())
    }

    testRequest("/go?q=bookmarks add nb https://newbookmark", user = "yuri")
  }

  @Test
  fun testSearchSuggestionsCommands() {
    runBlocking {
      services.providerConfigStore.store("yuri@coo.ee", "test", mapOf())
    }

    testRequest("/api/v0/search-suggestion?q=tes", expectedCode = OK, user = "yuri") {
      assertEquals(
        "[\"tes\"," +
          "[\"test\"]," +
          "[\"Command for 'test'\"]," +
          "[\"https://coo.ee/go?q=test\"]]",
        response.content
      )
    }
  }

  @Test
  fun testSearchSuggestionsArguments() {
    runBlocking {
      services.providerConfigStore.store("yuri@coo.ee", "test", mapOf())
    }

    testRequest("/api/v0/search-suggestion?q=test+", expectedCode = OK, user = "yuri") {
      assertEquals(
        "[\"test \"," +
          "[\"test aaa\",\"test bbb\"]," +
          "[\"Description for 'aaa'\",\"Description for 'bbb'\"]," +
          "[\"https://coo.ee/go?q=test+aaa\",\"https://coo.ee/go?q=test+bbb\"]]",
        response.content
      )
    }
  }

  private fun testRequest(
    path: String,
    method: HttpMethod = Get,
    user: String? = null,
    expectedCode: HttpStatusCode = OK,
    appConfigurer: Application.() -> Unit = { test() },
    fn: TestApplicationCall.() -> Unit = {}
  ) = withTestApplication({
    appConfigurer(this)
  }) {
    testSingleRequest(path, method, user, expectedCode, fn)
  }

  private fun TestApplicationEngine.testSingleRequest(
    path: String,
    method: HttpMethod = Get,
    user: String? = null,
    expectedCode: HttpStatusCode = OK,
    fn: TestApplicationCall.() -> Unit = {}
  ) {
    handleRequest(method, path) {
      if (user != null) {
        val token = services.userAuthenticator.tokenFor(user)
        addHeader("Authorization", "Bearer $token")
      }
    }.apply {
      assertEquals(expectedCode, response.status())
      fn(this)
    }
  }

  companion object {
    @ExperimentalCoroutinesApi
    @BeforeClass
    @JvmStatic
    fun installDebugHook() {
      DebugProbes.install()
    }
  }
}
