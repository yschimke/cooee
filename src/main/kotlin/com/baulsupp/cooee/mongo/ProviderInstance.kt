package com.baulsupp.cooee.mongo

data class ProviderInstance(val email: String?, val providerName: String, val config: Map<String, Any>)
