package com.baulsupp.cooee.services.github

import com.baulsupp.cooee.CooeeApplication
import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.TokenResponse
import com.baulsupp.cooee.p.TokenUpdate
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Token
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.credentials.ServiceDefinition
import com.baulsupp.okurl.credentials.SimpleCredentialsStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GithubProviderTest {
  val localCache = LocalCache()
  val app = CooeeApplication()
  val okHttpClient = app.client()
  val client = app.githubApolloClient(okHttpClient)
  val clientApi = object : ClientApi {
    override suspend fun tokenRequest(request: TokenRequest): TokenResponse {
      return TokenResponse(token = TokenUpdate(service = "github", token = token()))
    }

    override suspend fun logToClient(log: LogRequest) {
    }
  }
  val p = GithubProvider(client)
  val credentialsStore = SimpleCredentialsStore

  suspend fun token(): String {
    val token = credentialsStore.get(p.serviceDefinition as ServiceDefinition<Any>, DefaultToken)
    return (token as Oauth2Token).accessToken
  }

  @BeforeEach
  fun init() = runBlocking {
    Assumptions.assumeTrue(credentialsStore.get(p.serviceDefinition as ServiceDefinition<Any>, DefaultToken) != null)
    p.init(this@GithubProviderTest.okHttpClient, this@GithubProviderTest.clientApi, this@GithubProviderTest.localCache)
  }

  @Test
  fun testGithubMatches() = runBlockingTest {
    assertTrue(p.matches("github"))
  }

  @Test
  fun testProjectMatches() = runBlocking {
    assertTrue(p.matches("yschimke/okurl"))
  }

  @Test
  @Disabled("Not implemented yet")
  fun testIssueMatches() = runBlocking {
    assertTrue(p.matches("yschimke/okurl#1"))
  }

  @Test
  fun testNonMatch() = runBlockingTest {
    assertFalse(p.matches("yschimke-okurl-1"))
  }
}