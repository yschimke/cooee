package com.baulsupp.cooee.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CooeeApplication

fun main(args: Array<String>) {
	runApplication<CooeeApplication>(*args)
}
