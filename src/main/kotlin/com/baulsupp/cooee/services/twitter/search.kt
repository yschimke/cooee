package com.baulsupp.cooee.services.twitter

import com.baulsupp.okurl.credentials.TokenValue
import com.baulsupp.okurl.kotlin.execute
import com.baulsupp.okurl.kotlin.request
import com.baulsupp.okurl.location.Location
import com.baulsupp.okurl.moshi.Rfc3339InstantJsonAdapter
import com.baulsupp.okurl.services.mapbox.model.MapboxLatLongAdapter
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.rsocket.demo.twitter.model.Tweet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date

val tweetMoshi = Moshi.Builder()
    .add(MapboxLatLongAdapter())
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
    .add(Rfc3339InstantJsonAdapter())
    .build()!!
val tweetAdapter = tweetMoshi.adapter(Tweet::class.java)!!
fun parseTweet(it: String): Tweet = tweetAdapter.fromJson(it)!!

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun TweetSearchProvider.searchTweets(query: String?): Flow<Tweet> {
  val tokenSet = token()
  val s = client.execute(
      request(
          url = "https://stream.twitter.com/1.1/statuses/filter.json?track=$query",
          tokenSet = tokenSet
      )
  )

  val r = s.body!!.source()
      .inputStream()
      .bufferedReader(StandardCharsets.UTF_8)

  return r.lineSequence()
      .asFlow()
      .onCompletion {
        s.close()
      }
      .mapNotNull {
        parseTweet(it)
      }
}
