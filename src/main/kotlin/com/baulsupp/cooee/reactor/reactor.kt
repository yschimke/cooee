package com.baulsupp.cooee.reactor

import kotlinx.coroutines.reactive.awaitSingle
import org.reactivestreams.Publisher
import reactor.core.publisher.toFlux

suspend fun <T : Any> Publisher<T>.awaitList(): List<T> = this.toFlux().collectList().awaitSingle()
