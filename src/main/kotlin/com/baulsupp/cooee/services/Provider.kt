package com.baulsupp.cooee.services

import com.baulsupp.cooee.api.ClientApi
import okhttp3.OkHttpClient

abstract class Provider(val name: String) : ProviderFunctions {
  lateinit var client: OkHttpClient
  lateinit var clientApi: ClientApi

  fun init(client: OkHttpClient, clientApi: ClientApi) {
    this.client = client
    this.clientApi = clientApi
  }
}
