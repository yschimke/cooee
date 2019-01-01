package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.okurl.kotlin.query
import okhttp3.OkHttpClient
import java.io.InterruptedIOException

data class Friend(val id_str: String, val screen_name: String, val name: String)
data class FriendsList(val users: List<Friend>)

class TwitterProvider(val client: OkHttpClient) : BaseProvider() {
  override val name = "twitter"

  override suspend fun go(command: String, args: List<String>): GoResult {
    val text = if (args.isNotEmpty()) "&text=" + args.joinToString(" ") else ""
    val screen_name = command.substring(1)

    val friend = client.query<Friend>("https://api.twitter.com/1.1/users/show.json?screen_name=$screen_name")

    return RedirectResult("https://m.twitter.com/messages/compose?recipient_id=${friend.id_str}$text")
  }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      return try {
        val friends =
          client.query<FriendsList>("https://api.twitter.com/1.1/friends/list.json?include_user_entities=false&count=200")

        friends.users.map { "@" + it.screen_name }.filter { it.startsWith(command) }
      } catch (e: Exception) {
        log.warn("Failed to suggest completions", e)
        listOf()
      }
    }

    override suspend fun matches(command: String): Boolean {
      // TODO exact match twitter username pattern
      return command.startsWith("@") && command.length > 1
    }
  }
}
