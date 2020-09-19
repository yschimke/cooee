package com.baulsupp.cooee.services.twitter

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.cache.LocalCache
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandStatus
import com.baulsupp.cooee.p.args
import com.baulsupp.cooee.p.arguments
import com.baulsupp.cooee.p.error
import com.baulsupp.cooee.p.redirect
import com.baulsupp.cooee.p.single_command
import com.baulsupp.cooee.services.Provider
import com.baulsupp.okurl.services.twitter.TwitterAuthInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient

class TweetSearchProvider : Provider("twitter:search", TwitterAuthInterceptor().serviceDefinition) {
  override suspend fun init(client: OkHttpClient, clientApi: ClientApi, cache: LocalCache) {
    super.init(client, clientApi, cache)
  }

  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    val query = request.arguments

    if (query.isEmpty()) {
      return flowOf(CommandResponse.error("no search params"))
    }

    return searchTweets(query.joinToString("+")).map {
      CommandResponse(message = it.text, status = CommandStatus.STREAM)
    }
  }

  override suspend fun matches(command: String): Boolean {
    return command == "twitter:search"
  }
}
