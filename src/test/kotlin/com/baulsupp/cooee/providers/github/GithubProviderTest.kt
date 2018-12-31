package com.baulsupp.cooee.providers.github

import org.junit.Test
import kotlin.test.assertEquals

class GithubProviderTest {
  val p = GithubProvider()

  @Test
  fun basic() {
    assertEquals("github", p.name)
  }
}
