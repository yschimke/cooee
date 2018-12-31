package com.baulsupp.cooee.providers.bookmarks

import com.baulsupp.cooee.providers.ProviderInstance
import com.baulsupp.cooee.test.TestAppServices
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class BookmarksProviderTest {
  val defaultBookmarks = BookmarksProvider()
  val services = TestAppServices()
  val userBookmarks =
    BookmarksProvider().apply {
      this.configure(
        ProviderInstance(
          "yuri",
          "bookmarks",
          mapOf("bookmarks" to mapOf<String, String>())
        ), services.providerStore
      )
    }

  @Test
  fun basic() {
    assertEquals("bookmarks", defaultBookmarks.name)
  }

  @Test
  fun defaultBookmarks() = runBlocking {
    assertEquals(
      listOf("google", "facebook", "twitter", "gmail", "bookmarks"),
      defaultBookmarks.commandCompleter().suggestCommands("")
    )
  }

  @Test
  fun userBookmarks() = runBlocking {
    assertEquals(listOf("bookmarks"), userBookmarks.commandCompleter().suggestCommands(""))
//    assertEquals(listOf("add", "remove"), userBookmarks.argumentCompleter().suggestArguments("bookmarks"))
  }
}
