package com.baulsupp.cooee.services.github

import com.apollographql.apollo.ApolloClient
import com.baulsupp.cooee.CooeeApplication
import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.junit5.ProviderServicesExtension
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandStatus
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.TokenRequest
import com.baulsupp.cooee.p.TokenResponse
import com.baulsupp.cooee.p.TokenUpdate
import com.baulsupp.okurl.Main
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Token
import com.baulsupp.okurl.credentials.CredentialsStore
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.credentials.ServiceDefinition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ProviderServicesExtension::class)
class GithubProviderTest {
  lateinit var provider: GithubProvider

  suspend fun token(
    credentialsStore: CredentialsStore,
    serviceDefinition: ServiceDefinition<*>
  ): String {
    val token = credentialsStore.get(serviceDefinition, DefaultToken)
    return (token as Oauth2Token).accessToken
  }

  @BeforeEach
  fun init(
    credentialsStore: CredentialsStore,
    apolloClient: ApolloClient,
    okHttpClient: OkHttpClient,
    localCache: LocalCache,
    app: CooeeApplication
  ) = runBlocking {
    // TODO fix
    Main.moshi = app.moshi()

    provider = GithubProvider(apolloClient)

    val clientApi = object : ClientApi {
      override suspend fun tokenRequest(request: TokenRequest): TokenResponse {
        return TokenResponse(
            token = TokenUpdate(service = provider.serviceDefinition!!.serviceName(),
                token = token(credentialsStore, provider.serviceDefinition!!)))
      }

      override suspend fun logToClient(log: LogRequest) {
      }
    }

    assumeTrue(credentialsStore.get(provider.serviceDefinition as ServiceDefinition<Any>,
        DefaultToken) != null)
    provider.init(okHttpClient, clientApi, localCache)
  }

  @Test
  fun testGithubMatches() = runBlockingTest {
    assertTrue(provider.matches("github"))
  }

  @Test
  fun testProjectMatches() = runBlockingTest {
    assertTrue(provider.matches("yschimke/okurl"))
  }

  @Test
  fun testIssueMatches() = runBlockingTest {
    assertTrue(provider.matches("yschimke/okurl#1"))
  }

  @Test
  fun testNonMatch() = runBlockingTest {
    assertFalse(provider.matches("yschimke-okurl-1"))
  }

  @Test
  fun testProject() = runBlocking {
    val response = provider.runCommand(CommandRequest(parsed_command = listOf("yschimke/cooee")))!!.first()

    assertEquals(CommandStatus.DONE, response.status)
    assertEquals("cooee: Coo.ee codebase", response.message)
  }

  @Test
  fun testReleases() = runBlocking {
    val response = provider.runCommand(CommandRequest(parsed_command = listOf("yschimke/okurl", "releases")))!!.first()

    assertEquals(CommandStatus.DONE, response.status)
    assertEquals(null, response.message)
    assertEquals(listOf("Release", "Date", "URL", "Assets"), response.table?.columns?.map { it.name })
    assertTrue(response.table?.columns?.first()?.values.orEmpty().size > 1)
  }
}