package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.*
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient

class TwitterProvider : BaseProvider() {
  override val name = "twitter"

  override fun associatedServices(): Set<String> = setOf("twitter")

  lateinit var friends: List<Friend>

  override suspend fun init(appServices: AppServices, user: UserEntry?) = coroutineScope {
    super.init(appServices, user)

    friends = queryFriends()
  }

  override suspend fun go(command: String, vararg args: String): GoResult {
    val screenName = command.substring(1)

    val friend = appServices.client.query<Friend>(
      "https://api.twitter.com/1.1/users/show.json?screen_name=$screenName",
      userToken
    )

    if (args.isNotEmpty()) {
      sendDm(client, friend.id_str, args.joinToString(" "))
    }

    return RedirectResult("https://m.twitter.com/messages/compose?recipient_id=${friend.id_str}")
  }

  private suspend fun sendDm(client: OkHttpClient, id_str: String, text: String) {
    client.execute(request("https://api.twitter.com/1.1/direct_messages/events/new.json", userToken) {
      postJsonBody(DmRequest.simple(id_str, text))
    })
  }


  override suspend fun suggest(command: String): List<Suggestion> {
    return TwitterSuggester(this).suggest(command)
  }

  override suspend fun matches(command: String): Boolean {
    val parts = command.split("\\s+".toRegex(), limit = 2)

    // TODO exact match twitter username pattern
    return parts[0].startsWith("@") && friends.any { it.screen_name == parts[0].substring(1) }
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
