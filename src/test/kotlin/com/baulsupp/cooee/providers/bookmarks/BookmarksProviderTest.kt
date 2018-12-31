package com.baulsupp.cooee.providers.bookmarks

import org.junit.Test
import kotlin.test.assertEquals

class BookmarksProviderTest {
  val p = BookmarksProvider()

  @Test
  fun basic() {
    assertEquals("bookmarks", p.name)
  }
}
