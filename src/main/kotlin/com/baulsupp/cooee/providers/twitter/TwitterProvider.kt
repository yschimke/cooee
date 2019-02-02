package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.End
import com.baulsupp.okurl.kotlin.Next
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryPages
import kotlinx.coroutines.coroutineScope

data class Friend(val id_str: String, val screen_name: String, val name: String)

data class FriendsList(
  val users: List<Friend>,
  val next_cursor_str: String
)

data class Friends(val users: List<Friend>)

class TwitterProvider : BaseProvider() {
  override val name = "twitter"

  override fun associatedServices(): Set<String> = setOf("twitter")

  lateinit var friends: List<Friend>

  override suspend fun init(appServices: AppServices, user: UserEntry?) = coroutineScope {
    super.init(appServices, user)

    friends = queryFriends()
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

  override suspend fun suggest(command: String): List<Suggestion> {
    return TwitterSuggester(this).suggest(command)
  }

  override suspend fun matches(command: String): Boolean {
    // TODO exact match twitter username pattern
    return command.startsWith("@") && friends.any { it.screen_name == command.substring(1) }
  }

  private suspend fun queryFriends(): List<Friend> {
    val url = "https://api.twitter.com/1.1/friends/list.json?include_user_entities=false&count=200"
    return appServices.cache.get(user?.email, name, "friends") {
      Friends(
        appServices.client.queryPages<FriendsList>(
          url = url,
          paginator = { if (next_cursor_str == "0") End else Next("$url&cursor=$next_cursor_str") },
          tokenSet = userToken
        ).map { it.users }.flatten()
      )
    }.users
  }
}
