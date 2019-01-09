package com.baulsupp.cooee

import com.baulsupp.cooee.api.SearchSuggestionsResults
import com.baulsupp.cooee.api.SearchSuggestionsResultsAdapter
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.ktor.application.Application
import io.ktor.application.ApplicationStopped
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DataConversion
import io.ktor.features.HttpsRedirect
import io.ktor.features.gzip
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Locations
import io.ktor.routing.routing
import org.conscrypt.Conscrypt
import java.security.Security
import java.util.*

@KtorExperimentalLocationsAPI
fun Application.cloud() {
  // TODO breaks Mongo
  // setupProvider()

  module(ProdAppServices(this), cloud = true)
}

@KtorExperimentalLocationsAPI
fun Application.module(appServices: AppServices, cloud: Boolean) {
  this.environment.monitor.subscribe(ApplicationStopped) {
    appServices.close()
  }

  install(ContentNegotiation) {
    moshi {
      add(SearchSuggestionsResults::class.java, SearchSuggestionsResultsAdapter())
      add(KotlinJsonAdapterFactory())
      add(Date::class.java, Rfc3339DateJsonAdapter())
    }
  }

  install(Locations)

  install(Compression) {
    gzip {
      priority = 1.0
    }
  }

  install(CORS) {
    anyHost()
  }
  install(CallLogging)
  install(DataConversion)
  install(AutoHeadResponse)

  if (cloud) {
    install(HttpsRedirect) {
      sslPort = 443
      permanentRedirect = true
    }
  }

  routing {
    if (application.log.isTraceEnabled) {
      trace { application.log.trace(it.buildText()) }
    }

    root(appServices)
  }
}

private fun setupProvider() {
  try {
    Security.insertProviderAt(Conscrypt.newProviderBuilder().provideTrustManager().build(), 1)
  } catch (e: NoClassDefFoundError) {
    // Drop back to JDK
  }
}
