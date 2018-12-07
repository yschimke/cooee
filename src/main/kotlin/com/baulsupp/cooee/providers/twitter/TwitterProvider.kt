package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.providers.Provider
import com.baulsupp.cooee.providers.Target
import okhttp3.OkHttpClient

class TwitterProvider(client: OkHttpClient) : Provider {
//  val client: OkHttpClient by lazy {
//    // TODO make this work well from Okurl
//    val twitterInterceptor = TwitterAuthInterceptor()
//    val credentials =
//      TwitterServiceDefinition().parseCredentialsString("xxxx")
//    val authenticator = Interceptor { chain ->
//      if (chain.request().url().host() == "api.twitter.com") {
//        runBlocking { twitterInterceptor.intercept(chain, credentials) }
//      } else {
//        chain.proceed(chain.request())
//      }
//    }
//
//    client.newBuilder().addNetworkInterceptor(authenticator).build()
//  }

  override suspend fun url(command: String, args: List<String>): GoResult {
    val text = if (args.isNotEmpty()) "&text=" + args.joinToString(" ") else ""

    // TODO lookup real user
    val userid = 735627895645691905

    return RedirectResult("https://m.twitter.com/messages/compose?recipient_id=$userid$text")
  }

  override suspend fun targets(command: String, args: List<String>): List<Target> = listOf()

  override suspend fun matches(command: String): Boolean {
    return command.startsWith("@")
  }
}
