package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.providers.Provider
import com.baulsupp.cooee.providers.Target
import com.baulsupp.okurl.kotlin.JSON
import com.baulsupp.okurl.kotlin.queryMap
import com.baulsupp.okurl.kotlin.request
import com.baulsupp.okurl.services.twitter.TwitterAuthInterceptor
import com.baulsupp.okurl.services.twitter.TwitterServiceDefinition
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody

class TwitterProvider(client: OkHttpClient) : Provider {
  val client: OkHttpClient by lazy {
    // TODO make this work well from Okurl
    val twitterInterceptor = TwitterAuthInterceptor()
    val credentials =
      TwitterServiceDefinition().parseCredentialsString("shoutcooee,qpbuHpA7ynkbrQlQjvX4WRiz8,WRdRBgNjEZuyoHrHscm84YY39O0qaV5CmmnmjfVy7cooTY8PYw,735627895645691905-vXjHTLBBs9d2DmnZWTbrwku3F0VEtwP,orULQK55JnC16cuLtbJ2rkMQ6KrUobDtgEhl0RZdlKIGH")
    val authenticator = Interceptor { chain ->
      if (chain.request().url().host() == "api.twitter.com") {
        runBlocking { twitterInterceptor.intercept(chain, credentials) }
      } else {
        chain.proceed(chain.request())
      }
    }

    client.newBuilder().addNetworkInterceptor(authenticator).build()
  }

  override suspend fun url(command: String, args: List<String>): GoResult {
    val recipient = command.substring(1)
    val text = args.joinToString(" ")

    val body = "{\"event\": {\"type\": \"message_create\", \"message_create\": {\"target\": {" +
      "\"recipient_id\": \"$recipient\"}, " +
      "\"message_data\": {\"text\": \"$text\"}}}}"

    println(body)

    client.queryMap<Any>(request("https://api.twitter.com/1.1/direct_messages/new.json") {
      post(
        RequestBody.create(
          JSON,
          body
        )
      )
    }
    )
    return Completed
  }

  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun matches(command: String): Boolean {
    return command.startsWith("@")
  }
}
