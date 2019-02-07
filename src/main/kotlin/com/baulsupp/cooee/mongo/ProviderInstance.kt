package com.baulsupp.cooee.mongo

data class ProviderInstance(val email: String?, val provider: String, val config: Map<String, Any>)
