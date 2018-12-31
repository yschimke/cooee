package com.baulsupp.cooee.providers.twitter

import okhttp3.OkHttpClient
import org.junit.Test
import kotlin.test.assertEquals

class TwitterProviderTest {
  val client = OkHttpClient()
  val p = TwitterProvider(client)

  @Test
  fun basic() {
    assertEquals("twitter", p.name)
  }
}
