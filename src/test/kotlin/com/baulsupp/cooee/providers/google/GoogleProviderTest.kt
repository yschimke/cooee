package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals

class GoogleProviderTest {
  val appServices = TestAppServices()
  val p = GoogleProvider().apply { init(this@GoogleProviderTest.appServices) }

  @Test
  fun basic() {
    assertEquals("google", p.name)
  }

  @Test
  fun defaultBookmarks() = runBlocking {
    assertEquals(
      listOf("g", "gl"),
      p.commandCompleter().suggestCommands("")
    )
  }

  @Test
  fun googleSearch() = runBlocking {
    assertEquals(RedirectResult("https://www.google.com/search?q=query+terms"), p.go("g", listOf("query", "terms")))
  }

  @Test
  fun luckySearch() = runBlocking {
    assertEquals(
      RedirectResult("https://www.google.com/search?q=query+terms&btnI"),
      p.go("gl", listOf("query", "terms"))
    )
  }

  @Test
  fun googleSuggest() = runBlocking {
    assertThat(
      p.argumentCompleter().suggestArguments("gl", "how to convert to".split(" ")),
      hasItem(containsString("how to convert to pdf"))
    )
  }

  @Test
  fun google() = runBlocking {
    assertEquals(RedirectResult("https://www.google.com/"), p.go("g"))
    assertEquals(RedirectResult("https://www.google.com/"), p.go("gl"))
  }
}
