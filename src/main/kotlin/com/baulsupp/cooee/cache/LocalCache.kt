@file:Suppress("UNCHECKED_CAST")

package com.baulsupp.cooee.cache

import com.baulsupp.okurl.credentials.Token
import java.util.concurrent.ConcurrentHashMap

class LocalCache {
  data class Key(val token: Token, val providerName: String, val key: String)

  val map = ConcurrentHashMap<Key, Any>()

  inline fun <reified T: Any> get(token: Token, providerName: String, key: String, fetcher: () -> T): T {
    return map.getOrPut(Key(token, providerName, key)) {
      fetcher()
    } as T
  }
}
