package com.baulsupp.cooee.services.strava

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
class StravaProviderTest {
  lateinit var provider: StravaProvider

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
    okHttpClient: OkHttpClient,
    localCache: LocalCache,
    app: CooeeApplication
  ) = runBlocking {
    // TODO fix
    Main.moshi = app.moshi()

    provider = StravaProvider()

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
  fun pageMatches() = runBlockingTest {
    assertTrue(provider.matches("strava"))
  }

  @Test
  fun otherNoMatches() = runBlockingTest {
    assertFalse(provider.matches("github"))
  }

  @Test
  fun page() = runBlockingTest {
    val response = provider.runCommand(CommandRequest(parsed_command = listOf("strava")))!!.first()

    assertEquals(CommandStatus.REDIRECT, response.status)
    assertEquals("https://www.strava.com/", response.url)
  }

  @Test
  @Disabled("login required")
  fun last() = runBlocking {
    val response =
        provider.runCommand(CommandRequest(parsed_command = listOf("strava", "last")))!!.first()

    assertEquals(CommandStatus.DONE, response.status)
    assertTrue(response.message!!.startsWith("Distance"))
  }
}