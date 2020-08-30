package com.baulsupp.cooee

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.wire.WireJsonAdapterFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class CooeeApplication {
  @Bean
  fun moshi() = Moshi.Builder()
      .add(WireJsonAdapterFactory())
      .add(KotlinJsonAdapterFactory())
      .build()
}

fun main(args: Array<String>) {
  runApplication<CooeeApplication>(*args)
}
