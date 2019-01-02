package com.baulsupp.cooee

import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.mapAdapter
import com.baulsupp.okurl.kotlin.moshi
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpMethod.Companion.Get
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.Found
import io.ktor.http.HttpStatusCode.Companion.OK
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
import kotlin.test.assertFalse
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
    testRequest("/api/v0/command-completion?q=g") {
      assertEquals("{\"completions\":[\"g\",\"gl\",\"google\",\"gmail\"]}", response.content)
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
    testRequest("/api/v0/argument-completion?q=TRANS-1234 ") {
      assertEquals(HttpStatusCode.OK, response.status())
      assertEquals("{\"completions\":[\"close\",\"comment\"]}", response.content)
    }
  }

  @Test
  fun testJiraProject() {
    testRequest("/go?q=TRANS", expectedCode = Found) {
      assertEquals("https://jira.atlassian.com/browse/TRANS", response.headers["Location"])
    }
  }

  @Test
  fun testJiraIssue() {
    testRequest("/go?q=TRANS-2474", expectedCode = Found) {
      assertEquals("https://jira.atlassian.com/browse/TRANS-2474", response.headers["Location"])
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

  @Test
  fun testBookmarkCompletion() {
    testCompletion("gm") {
      assertEquals(listOf("gmail"), it)
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
    registerUser("yuri")

    testRequest("/api/v0/user", user = "yuri") {
      assertEquals("{\"email\":\"yuri@schimke.ee\",\"token\":\"$token\",\"user\":\"yuri\"}", response.content)
    }
  }

  private fun registerUser(name: String, token: String? = null) {
    runBlocking {
      services.userStore.storeUser(
        UserEntry(
          token ?: services.userAuthenticator.tokenFor("yuri"),
          name,
          "$name@schimke.ee"
        )
      )
    }
  }

  @Test
  fun testLogin() {
    testRequest("/login?user=yuri&callback=http://localhost:3000/callback", expectedCode = Found) {
      assertEquals(
        "http://localhost:3000/callback?code=eyJhbGciOiJub25lIn0.eyJ1c2VyIjoieXVyaSJ9.",
        response.headers["Location"]
      )
    }
  }

  @Test
  fun testAddBookmarkProvider() {
    registerUser("yuri")

    testRequest("/go?q=add bookmarks", user = "yuri")

    assertEquals(1, services.providerStore.providerInstances.size)
  }

  @Test
  fun testAddBookmarkName() {
    registerUser("yuri")

    runBlocking {
      services.providerStore.store(ProviderInstance("yuri", "bookmarks", mapOf()))
    }

    testRequest("/go?q=bookmarks add nb https://newbookmark", user = "yuri")
  }

  @Test
  fun testSearchSuggestions() {
    testRequest("/api/v0/search-suggestion?q=ABC", expectedCode = OK) {
      assertEquals(" [\"ABC\",\n" +
        "  [\"ABC-A\",\n" +
        "   \"ABC-B\",\n" +
        "   \"ABC-C\"],\n" +
        "  [\"Desc A\",\n" +
        "   \"Desc B\",\n" +
        "   \"Desc C\"],\n" +
        "  [\"https://coo.ee/go?q=ABC-A\",\n" +
        "   \"https://coo.ee/go?q=ABC-B\",\n" +
        "   \"https://coo.ee/go?q=ABC-C\"]]", response.content)
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
