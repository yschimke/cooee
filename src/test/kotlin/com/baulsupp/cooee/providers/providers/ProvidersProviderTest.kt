package com.baulsupp.cooee.providers.providers

import org.junit.Test
import kotlin.test.assertEquals

class ProvidersProviderTest {
  val p = ProvidersProvider()

  @Test
  fun basic() {
    assertEquals("providers", p.name)
  }
}
