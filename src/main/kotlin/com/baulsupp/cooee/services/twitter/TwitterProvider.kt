package com.baulsupp.cooee.services.twitter

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.args
import com.baulsupp.cooee.p.redirect
import com.baulsupp.cooee.p.single_command
import com.baulsupp.cooee.services.Provider
import com.baulsupp.okurl.services.twitter.TwitterAuthInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient

class TwitterProvider : Provider("twitter", TwitterAuthInterceptor().serviceDefinition) {
  lateinit var friends: List<Friend>

  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    initFriends()

    val command = request.single_command ?: return null
    val screenName = command.substring(1)

    val friend = friends.find { it.screen_name == screenName } ?: return null

    if (request.args.isNotEmpty()) {
      sendDm(client, friend.id_str, request.args.joinToString(" "))
      return flowOf(CommandResponse.redirect("dm sent"))
    }

    return flowOf(CommandResponse.redirect("https://m.twitter.com/messages/compose?recipient_id=${friend.id_str}"))
  }

  override suspend fun matches(command: String): Boolean {
    initFriends()

    val parts = command.split("\\s+".toRegex(), limit = 2)

    // TODO exact match twitter username pattern
    return parts[0].startsWith("@") && friends.any { it.screen_name == parts[0].substring(1) }
  }

  private suspend fun initFriends() {
    if (!this::friends.isInitialized) {
      friends = queryFriends()
    }
  }
}
