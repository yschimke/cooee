package com.baulsupp.cooee

import com.baulsupp.cooee.api.CompletionItem
import com.baulsupp.cooee.api.Completions
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.okurl.kotlin.moshi
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpMethod.Companion.Delete
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.TestApplicationCall
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertThat
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
  private val services = TestAppServices()

  fun Application.test() = module(services, false)

  @Test
  fun testGoCommandWithSpaces() {
    testRequest("/api/v0/goinfo?q=g abc") {
      assertThat(response.content, containsString("https://www.google.com/search?q=abc"))
    }
  }

  @Test
  fun testGoCommandWithPlus() {
    testRequest("/api/v0/goinfo?q=g+yriu") {
      assertThat(response.content, containsString("https://www.google.com/search?q=yriu"))
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
      services.providerConfigStore.store(
        "yuri@coo.ee",
        "bookmarks",
        mapOf("bookmarks" to BookmarksProvider.defaultBookmarks)
      )
    }

    testRequest("/api/v0/completion?q=g", user = "yuri") {
      assertEquals(
        "{\"completions\":[" +
          "{\"word\":\"g\",\"line\":\"g\",\"description\":\"Command for 'g'\",\"provider\":\"google\",\"suggestion\":{\"line\":\"g\",\"provider\":\"google\",\"description\":\"Command for 'g'\",\"type\":\"UNKNOWN\"}}," +
          "{\"word\":\"gl\",\"line\":\"gl\",\"description\":\"Command for 'gl'\",\"provider\":\"google\",\"suggestion\":{\"line\":\"gl\",\"provider\":\"google\",\"description\":\"Command for 'gl'\",\"type\":\"UNKNOWN\"}}," +
          "{\"word\":\"google\",\"line\":\"google\",\"description\":\"https://google.com\",\"provider\":\"bookmarks\",\"suggestion\":{\"line\":\"google\",\"provider\":\"bookmarks\",\"description\":\"https://google.com\",\"type\":\"LINK\",\"url\":\"https://google.com\"}}," +
          "{\"word\":\"gmail\",\"line\":\"gmail\",\"description\":\"https://mail.google.com\",\"provider\":\"bookmarks\",\"suggestion\":{\"line\":\"gmail\",\"provider\":\"bookmarks\",\"description\":\"https://mail.google.com\",\"type\":\"LINK\",\"url\":\"https://mail.google.com\"}}]}",
        response.content
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
        "{\"completions\":[{\"word\":\"test\",\"line\":\"test\",\"description\":\"Command for 'test'\",\"provider\":\"test\",\"suggestion\":{\"line\":\"test\",\"provider\":\"test\",\"description\":\"Command for 'test'\",\"type\":\"UNKNOWN\"}}]}",
        response.content
      )
    }
  }

  @KtorExperimentalLocationsAPI
  @Test
  fun testBookmarkCompletion() {
    testCompletion("gm") {
      assertEquals(
        Completions(
          listOf(
            CompletionItem(
              "gmail",
              "gmail",
              "https://mail.google.com",
              provider = "bookmarks",
              suggestion = Suggestion(
                "gmail",
                "bookmarks",
                "https://mail.google.com",
                type = SuggestionType.LINK,
                url = "https://mail.google.com"
              )
            )
          )
        ),
        it
      )
    }
  }

  @Test
  fun testGmailBookmark() {
    testRequest("/api/v0/goinfo?q=gmail") {
      assertThat(response.content, containsString("https://mail.google.com"))
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
  fun testAddBookmarkName() {
    runBlocking {
      services.providerConfigStore.store("yuri@coo.ee", "bookmarks", mapOf())
    }

    testRequest("/api/v0/goinfo?q=bookmarks add nb https://newbookmark")
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
          "[\"Command for 'test aaa'\",\"Command for 'test bbb'\"]," +
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

  @Test
  fun testProviders() {
    testRequest("/api/v0/providers", user = "yuri") {
      assertEquals(
        "{\"providers\":[" +
          "{\"name\":\"cooee\",\"installed\":true,\"config\":{},\"services\":[]}," +
          "{\"name\":\"google\",\"installed\":false,\"services\":[]}," +
          "{\"name\":\"github\",\"installed\":false,\"services\":[\"github\"]}," +
          "{\"name\":\"twitter\",\"installed\":false,\"services\":[\"twitter\"]}," +
          "{\"name\":\"bookmarks\",\"installed\":false,\"services\":[]}," +
          "{\"name\":\"gmail\",\"installed\":false,\"services\":[\"google\"]}," +
          "{\"name\":\"trello\",\"installed\":false,\"services\":[\"trello\"]}," +
          "{\"name\":\"jira\",\"installed\":false,\"services\":[\"atlassian\"]}," +
          "{\"name\":\"test\",\"installed\":false,\"services\":[]}" +
          "]}",
        response.content
      )
    }
  }

  @Test
  fun testProviderRequest() {
    testRequest("/api/v0/provider/cooee", user = "yuri") {
      assertEquals(
        "{\"name\":\"cooee\",\"installed\":true,\"config\":{},\"services\":[]}",
        response.content
      )
    }
  }

  @Test
  fun testProviderDeleteRequest() {
    runBlocking {
      services.providerConfigStore.store(
        "yuri@coo.ee",
        "bookmarks",
        mapOf("bookmarks" to BookmarksProvider.defaultBookmarks)
      )
    }

    assertEquals(1, services.providerConfigStore.providerInstances.size)

    testRequest("/api/v0/provider/bookmarks", user = "yuri", method = Delete)

    assertEquals(0, services.providerConfigStore.providerInstances.size)
  }

  @Test
  fun testProviderPutRequest() {
    assertEquals(0, services.providerConfigStore.providerInstances.size)

    withTestApplication({ test() }) {
      handleRequest(HttpMethod.Put, "/api/v0/provider/bookmarks") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody("{\"config\":{\"bookmarks\":{\"a\": \"http://a.com\"}}}")
        val token = services.userAuthenticator.tokenFor("yuri")
        addHeader("Authorization", "Bearer $token")
      }.apply {
        assertEquals(HttpStatusCode.OK, response.status())
      }
    }

    assertEquals(1, services.providerConfigStore.providerInstances.size)
  }

  @Test
  fun testProviderRequestNotFound() {
    testRequest("/api/v0/provider/cooee2", user = "yuri", expectedCode = NotFound)
  }

  @Test
  fun testProviderRequestNotInstalled() {
    testRequest("/api/v0/provider/test", user = "yuri") {
      assertEquals(
        "{\"name\":\"test\",\"installed\":false,\"services\":[]}",
        response.content
      )
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
