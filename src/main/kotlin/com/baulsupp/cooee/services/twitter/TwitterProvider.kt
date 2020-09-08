package com.baulsupp.cooee.services.twitter

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.*
import com.baulsupp.cooee.services.Provider
import okhttp3.OkHttpClient

class TwitterProvider : Provider("twitter") {
  lateinit var friends: List<Friend>

  override suspend fun init(client: OkHttpClient, clientApi: ClientApi, cache: LocalCache) {
    super.init(client, clientApi, cache)

    token = token()

    friends = queryFriends()
  }

  override suspend fun runCommand(request: CommandRequest): CommandResponse? {
    val screenName = request.single_command.substring(1)

    val friend = friends.find { it.screen_name == screenName } ?: return null

    if (request.args.isNotEmpty()) {
      sendDm(client, friend.id_str, request.args.joinToString(" "))
      return CommandResponse.redirect("dm sent")
    }

    return CommandResponse.redirect("https://m.twitter.com/messages/compose?recipient_id=${friend.id_str}")
  }

  override suspend fun matches(command: String): Boolean {
    val parts = command.split("\\s+".toRegex(), limit = 2)

    // TODO exact match twitter username pattern
    return parts[0].startsWith("@") && friends.any { it.screen_name == parts[0].substring(1) }
  }
}
