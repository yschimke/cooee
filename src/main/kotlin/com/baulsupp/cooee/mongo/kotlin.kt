package com.baulsupp.cooee.mongo

import org.reactivestreams.Publisher

suspend fun <T> Publisher<List<T>>.awaitList(): List<T> {
  TODO()
}
