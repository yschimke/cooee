package com.baulsupp.cooee.services.github

import com.baulsupp.okurl.Main.Companion.moshi
import com.baulsupp.okurl.kotlin.execute
import com.baulsupp.okurl.kotlin.listAdapter
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.kotlin.request
import com.squareup.moshi.Moshi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

suspend inline fun <reified T> okhttp3.OkHttpClient.queryGithubPages(
  url: String,
  tokenSet: com.baulsupp.okurl.credentials.Token = com.baulsupp.okurl.credentials.DefaultToken
): List<T> = kotlinx.coroutines.coroutineScope {
  val firstRequest = request(url, tokenSet)

  val firstResponse = execute(firstRequest)

  val linkHeader = firstResponse.header("Link")

  val results = mutableListOf<T>()

  firstResponse.body.use {
    results.addAll(moshi.listAdapter<T>().fromJson(it!!.string())!!)
  }

  if (linkHeader != null) {
    val re = "page=(\\d+)".toRegex()

    val lastPage = re.findAll(linkHeader).map { it.groupValues.last().toInt() }.lastOrNull()

    if (lastPage != null) {
      val responses = (2..lastPage).map {
        async {
          // TODO check for ?
          queryList<T>("$url&page=$it", tokenSet)
        }
      }

      results.addAll(responses.awaitAll().flatten())
    }
  }

  results
}
