package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.query
import kotlinx.coroutines.coroutineScope

data class Friend(val id_str: String, val screen_name: String, val name: String)
data class FriendsList(val users: List<Friend>)

class TwitterProvider : BaseProvider() {
  override val name = "twitter"

  override fun associatedServices(): Set<String> = setOf("twitter")

  lateinit var friends: List<Friend>

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    super.init(appServices, user)

    coroutineScope {
      friends = queryFriends()
    }
  }

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
    override suspend fun suggestCommands(command: String): List<Suggestion> {
      return when {
          command.isEmpty() || !command.startsWith("@") -> listOf()
          else -> try {
            friends.map { "@" + it.screen_name }.filter { it.startsWith(command, ignoreCase = true) }
          } catch (e: Exception) {
            log.warn("Failed to suggest completions", e)
            listOf<String>()
          }.map { Suggestion(it, type = SuggestionType.COMMAND, description = "DM $it") }
      }
    }

    override suspend fun matches(command: String): Boolean {
      // TODO exact match twitter username pattern
      return command.startsWith("@") && friends.any { it.screen_name == command.substring(1) }
    }
  }

  private suspend fun queryFriends(): List<Friend> {
    return appServices.cache.get(user?.email, name, "friends") {
      appServices.client.query<FriendsList>(
        "https://api.twitter.com/1.1/friends/list.json?include_user_entities=false&count=200",
        userToken
      )
    }.users
  }
}
