package com.baulsupp.cooee.providers.google

import org.junit.Test
import kotlin.test.assertEquals

class GoogleProviderTest {
  val p = GoogleProvider()

  @Test
  fun basic() {
    assertEquals("google", p.name)
  }
}
