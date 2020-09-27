@file:Suppress("UNCHECKED_CAST")

package com.baulsupp.cooee.cache

import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.ConcurrentHashMap

class AuthFlowCache {
  data class Key(val state: String)

  val map = ConcurrentHashMap<Key, CompletableDeferred<String>>()
  val dataMap = ConcurrentHashMap<Key, Map<String, String>>()

  fun get(state: String): CompletableDeferred<String> {
    return map.getOrPut(Key(state)) {
      CompletableDeferred()
    }
  }

  fun storeData(state: String, map: Map<String, String>) {
    dataMap[Key(state)] = map
  }

  fun findData(state: String): Map<String, String>? {
    return dataMap[Key(state)]
  }
}
