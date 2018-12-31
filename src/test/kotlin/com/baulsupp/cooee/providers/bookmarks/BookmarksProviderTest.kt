package com.baulsupp.cooee.providers.bookmarks

import com.baulsupp.cooee.api.RedirectResult
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
      init(services)
      this.configure(
        ProviderInstance(
          "yuri",
          "bookmarks",
          mapOf("bookmarks" to mapOf<String, String>())
        )
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
    assertEquals(listOf("add", "remove"), userBookmarks.argumentCompleter().suggestArguments("bookmarks"))

    configureBookmarks("t" to "https://test.com/")

    assertEquals(listOf("t", "bookmarks"), userBookmarks.commandCompleter().suggestCommands(""))
    assertEquals(listOf("remove t", "add", "remove"), userBookmarks.argumentCompleter().suggestArguments("bookmarks"))
  }

  @Test
  fun userBookmarkDirect() = runBlocking {
    configureBookmarks("t" to "https://test.com/")

    assertEquals(RedirectResult("https://test.com/"), userBookmarks.go("t"))
  }

  private fun configureBookmarks(vararg pairs: Pair<String, String>) {
    userBookmarks.instance = userBookmarks.instance?.copy(config = mapOf("bookmarks" to mapOf(*pairs)))
  }

  @Test
  fun userBookmarkSearch() = runBlocking {
    configureBookmarks("t" to "https://test.com/q=%s")

    assertEquals(RedirectResult("https://test.com/q=query"), userBookmarks.go("t", listOf("query")))
  }
}
