package com.baulsupp.cooee

import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.services.CombinedProvider
import com.baulsupp.cooee.services.github.GithubProvider
import com.baulsupp.cooee.services.strava.StravaProvider
import com.baulsupp.okurl.Main
import com.baulsupp.okurl.authenticator.AuthenticatingInterceptor
import com.baulsupp.okurl.credentials.CredentialsStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.wire.WireJsonAdapterFactory
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.SpringBootApplication
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

    return builder.build()
  }

  @Bean
  fun combinedProvider() = CombinedProvider(StravaProvider(), GithubProvider())

  @EventListener(classes = [ApplicationStartedEvent::class])
  fun onApplicationEvent(event: ApplicationStartedEvent) {
    Main.moshi = moshi()
  }
}

fun main(args: Array<String>) {
  runApplication<CooeeApplication>(*args)
}
