package com.baulsupp.cooee.services.twitter

import com.baulsupp.okurl.kotlin.End
import com.baulsupp.okurl.kotlin.Next
import com.baulsupp.okurl.kotlin.execute
import com.baulsupp.okurl.kotlin.postJsonBody
import com.baulsupp.okurl.kotlin.queryPages
import com.baulsupp.okurl.kotlin.request
import okhttp3.OkHttpClient

suspend fun TwitterProvider.queryFriends(): List<Friend> {
  val url = "https://api.twitter.com/1.1/friends/list.json?include_user_entities=false&count=200"
  return cache.get(token(), name, "friends") {
    Friends(
        client.queryPages<FriendsList>(
            url = url,
            paginator = { if (next_cursor_str == "0") End else Next("$url&cursor=$next_cursor_str") },
            tokenSet = token()
        ).map { it.users }.flatten()
    )
  }.users
}

suspend fun TwitterProvider.sendDm(client: OkHttpClient, id_str: String, text: String) {
  client.execute(request("https://api.twitter.com/1.1/direct_messages/events/new.json", token()) {
    postJsonBody(DmRequest.simple(id_str, text))
  })
}


