package com.baulsupp.cooee

import com.apollographql.apollo.ApolloClient
import com.baulsupp.cooee.cache.AuthFlowCache
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.config.ProviderProperties
import com.baulsupp.cooee.services.CombinedProvider
import com.baulsupp.cooee.services.cooee.CooeeProvider
import com.baulsupp.cooee.services.cooee.LoginProvider
import com.baulsupp.cooee.services.dev.DevCommandProvider
import com.baulsupp.cooee.services.dev.DevTableProvider
import com.baulsupp.cooee.services.github.GithubProvider
import com.baulsupp.cooee.services.strava.StravaProvider
import com.baulsupp.cooee.services.twitter.TweetSearchProvider
import com.baulsupp.cooee.services.twitter.TwitterProvider
import com.baulsupp.cooee.util.WireProto3PropertyNamingStrategy
import com.baulsupp.okurl.Main
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.credentials.CredentialsStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.wire.WireJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener

@SpringBootApplication
class CooeeApplication {
  @Bean
  fun moshi() = Moshi.Builder()
      .add(WireJsonAdapterFactory())
      .add(KotlinJsonAdapterFactory())
      .build()

  @Bean
  fun cache() = LocalCache()

  @Bean
  fun client(): OkHttpClient {
    val builder = OkHttpClient.Builder()

    val authenticatingInterceptor = AuthenticatingInterceptor(CredentialsStore.NONE)
    builder.addNetworkInterceptor(authenticatingInterceptor)

//    builder.addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))

    return builder.build()
  }

  @Bean
  fun githubApolloClient(okHttpClient: OkHttpClient) = ApolloClient.builder()
      .serverUrl("https://api.github.com/graphql")
      .okHttpClient(okHttpClient)
      .build()

  @Bean
  fun authCache() = AuthFlowCache()

  @Bean
  fun providerSecrets() = ProviderProperties()

  @Bean
  fun combinedProvider(authFlowCache: AuthFlowCache, apolloClient: ApolloClient) =
      CombinedProvider(StravaProvider(), GithubProvider(apolloClient), LoginProvider(
          authFlowCache, providerSecrets()), CooeeProvider(),
          DevCommandProvider(), DevTableProvider(),
          TweetSearchProvider(), TwitterProvider(),
      )

  @EventListener(classes = [ApplicationStartedEvent::class])
  fun onApplicationEvent(event: ApplicationStartedEvent) {
    Main.moshi = moshi()
  }

  @Bean
  fun jsonCustomizer() = Jackson2ObjectMapperBuilderCustomizer {
    it.propertyNamingStrategy(WireProto3PropertyNamingStrategy())
  }
}

fun main(args: Array<String>) {
  runApplication<CooeeApplication>(*args) {
    setBannerMode(Banner.Mode.OFF)
  }
}
