package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.authentication.AuthenticationData
import com.baulsupp.cooee.authentication.AuthenticationFlowCache
import com.baulsupp.okurl.authenticator.authflow.AuthOption
import com.baulsupp.okurl.authenticator.authflow.Callback
import com.baulsupp.okurl.authenticator.authflow.Prompt
import com.baulsupp.okurl.authenticator.authflow.State
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Flow
import com.baulsupp.okurl.credentials.NoToken
import com.baulsupp.okurl.kotlin.queryForString
import com.baulsupp.okurl.kotlin.requestBuilder
import com.baulsupp.okurl.services.twitter.TwitterCredentials
import com.baulsupp.okurl.services.twitter.TwitterServiceDefinition
import com.baulsupp.okurl.services.twitter.joauth.KeyValueHandler
import com.baulsupp.okurl.services.twitter.joauth.Signature
import com.baulsupp.okurl.services.twitter.joauth.StandardKeyValueParser
import okhttp3.FormBody

class TwitterAuthFlow(val authenticationFlowCache: AuthenticationFlowCache) :
  Oauth2Flow<TwitterCredentials>(TwitterServiceDefinition()) {
  override suspend fun start(): String {
    val consumerKey = options["twitter.consumerKey"] as String
    val consumerSecret = options["twitter.consumerSecret"] as String
    val state = options["state"] as String
    val callback = options["callback"] as String + "?state=$state"

    val unauthed = TwitterCredentials(null, consumerKey, consumerSecret, null, "")

    val requestCredentials = generateRequestToken(unauthed, callback)

    authenticationFlowCache.storeData(
      AuthenticationData(
        state,
        mapOf("requestCredentials" to serviceDefinition.formatCredentialsString(requestCredentials))
      )
    )

    return "https://api.twitter.com/oauth/authenticate?oauth_token=${requestCredentials.token}"
  }

  override suspend fun complete(code: String): TwitterCredentials {
    val consumerKey = options["twitter.consumerKey"] as String
    val consumerSecret = options["twitter.consumerSecret"] as String
    val state = options["state"] as String

    val requestCredentialsString = authenticationFlowCache.findData(state)?.data?.get("requestCredentials")

    // TODO handle missing
    val requestCredentials = serviceDefinition.parseCredentialsString(requestCredentialsString as String)

    val body = FormBody.Builder().add("oauth_verifier", code).build()
    var request = requestBuilder(
      "https://api.twitter.com/oauth/access_token",
      NoToken
    )
      .post(body)
      .build()
    request = request.newBuilder()
      .header(
        "Authorization",
        Signature().generateAuthorization(request, requestCredentials)
      )
      .build()
    val tokenMap = parseTokenMap(client.queryForString(request))
    return TwitterCredentials(
      tokenMap["screen_name"], consumerKey,
      consumerSecret,
      tokenMap["oauth_token"], tokenMap["oauth_token_secret"]
    )
  }

  override fun options(): List<AuthOption<*>> {
    return listOf(
      Prompt("twitter.consumerKey", "Consumer Key", null, false),
      Prompt("twitter.consumerSecret", "Consumer Secret", null, true),
      Callback,
      State
    )
  }

  suspend fun generateRequestToken(
    unauthed: TwitterCredentials,
    callback: String
  ): TwitterCredentials {
    val body = FormBody.Builder().add("oauth_callback", callback).build()
    var request = requestBuilder(
      "https://api.twitter.com/oauth/request_token",
      NoToken
    )
      .post(body)
      .build()

    request = request.newBuilder()
      .header(
        "Authorization",
        Signature().generateAuthorization(request, unauthed)
      )
      .build()

    val tokenMap = parseTokenMap(client.queryForString(request))

    return TwitterCredentials(
      unauthed.username, unauthed.consumerKey,
      unauthed.consumerSecret,
      tokenMap["oauth_token"], tokenMap["oauth_token_secret"]
    )
  }

  protected fun parseTokenMap(tokenDetails: String): Map<String, String> {
    val handler = KeyValueHandler.SingleKeyValueHandler()

    val bodyParser = StandardKeyValueParser("&", "=")
    bodyParser.parse(tokenDetails, listOf<KeyValueHandler>(handler))

    return handler.toMap()
  }
}
