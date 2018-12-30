package com.baulsupp.cooee.okhttp

import com.baulsupp.okurl.credentials.NoToken
import com.baulsupp.okurl.credentials.Token
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Response
import java.io.IOException
import java.time.Duration
import java.time.Instant

class HoneycombEventListenerFactory() : EventListener.Factory {
  override fun create(call: Call): EventListener {
    return HoneycombEventListener(call)
  }
}

class HoneycombEventListener(val call: Call) : EventListener() {
  private lateinit var start: Instant
  private var status: Int? = null

  override fun callStart(call: Call) {
    start = Instant.now()
  }

  override fun responseHeadersEnd(call: Call, response: Response) {
    status = response.code()
  }

  override fun callEnd(call: Call) {
    val took = Duration.between(start, Instant.now())
    val token = call.request().tag(Token::class.java) ?: NoToken

    val dataMap = mapOf(
      "duration" to took.toMillis(),
      "token" to token.javaClass.simpleName,
      "host" to call.request().url().host(),
      "url" to call.request().url().toString(),
      "method" to call.request().method(),
      "response" to status
    )

//    honeyClient.createEvent().addFields(dataMap).setDataset("httpcall").send()
  }

  override fun callFailed(call: Call, ioe: IOException) {
    val took = Duration.between(start, Instant.now())
    val token = call.request().tag(Token::class.java) ?: NoToken

    val dataMap = mapOf(
      "duration" to took.toMillis(),
      "token" to token.javaClass.simpleName,
      "host" to call.request().url().host(),
      "url" to call.request().url().toString(),
      "method" to call.request().method(),
      "exception" to ioe.toString()
    )

//    honeyClient.createEvent().addFields(dataMap).setDataset("httpcall").send()
  }
}
