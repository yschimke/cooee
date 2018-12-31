package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.RedirectResult
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class GoogleProviderTest {
  val p = GoogleProvider()

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
  fun google() = runBlocking {
    assertEquals(RedirectResult("https://www.google.com/"), p.go("g"))
    assertEquals(RedirectResult("https://www.google.com/"), p.go("gl"))
  }
}
