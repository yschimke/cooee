package com.baulsupp.cooee.providers.bookmarks

import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.users.UserEntry
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class BookmarksProviderTest {
  val defaultBookmarks = BookmarksProvider.loggedOut()
  val services = TestAppServices()
  val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")
  val userBookmarks =
    BookmarksProvider().apply {
      init(services, userEntry)
      configure(mapOf("bookmarks" to mapOf<String, String>()))
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
    userBookmarks.config["bookmarks"] = mapOf(*pairs)
  }

  @Test
  fun userBookmarkSearch() = runBlocking {
    configureBookmarks("t" to "https://test.com/q=%s")

    assertEquals(RedirectResult("https://test.com/q=query"), userBookmarks.go("t", "query"))
  }
}
