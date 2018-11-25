package com.baulsupp.cooee.api

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/go2")
data class Go(val query: String?)