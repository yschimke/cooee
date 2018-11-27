package com.baulsupp.cooee.api

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/go")
data class Go(val q: String? = null)
