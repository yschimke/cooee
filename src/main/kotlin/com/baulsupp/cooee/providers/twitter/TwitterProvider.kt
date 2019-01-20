package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.Completion
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.okurl.kotlin.query

data class Friend(val id_str: String, val screen_name: String, val name: String)
data class FriendsList(val users: List<Friend>)

class TwitterProvider : BaseProvider() {
  override val name = "twitter"

  override fun associatedServices(): Set<String> = setOf("twitter")

  override suspend fun go(command: String, vararg args: String): GoResult {
    val text = if (args.isNotEmpty()) "&text=" + args.joinToString(" ") else ""
    val screenName = command.substring(1)

    val friend = appServices.client.query<Friend>(
      "https://api.twitter.com/1.1/users/show.json?screen_name=$screenName",
      userToken
    )

    return RedirectResult("https://m.twitter.com/messages/compose?recipient_id=${friend.id_str}$text")
  }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<Completion> {
      return try {
        val friends =
          appServices.client.query<FriendsList>(
            "https://api.twitter.com/1.1/friends/list.json?include_user_entities=false&count=200",
            userToken
          )

        if (command == "") {
          return friends.users.map { "@" + it.screen_name.substring(0, 1) }.distinct().map { Completion(it) }
        }

        friends.users.map { "@" + it.screen_name }.filter { it.startsWith(command, ignoreCase = true) }
      } catch (e: Exception) {
        log.warn("Failed to suggest completions", e)
        listOf<String>()
      }.map { Completion(it) }
    }

    override suspend fun matches(command: String): Boolean {
      // TODO exact match twitter username pattern
      return command.startsWith("@") && command.length > 1
    }
  }
}
