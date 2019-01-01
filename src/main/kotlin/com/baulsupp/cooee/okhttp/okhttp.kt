package com.baulsupp.cooee.okhttp

import okhttp3.OkHttpClient

fun OkHttpClient.close() {
  connectionPool().evictAll()
  dispatcher().executorService().shutdown()
}
