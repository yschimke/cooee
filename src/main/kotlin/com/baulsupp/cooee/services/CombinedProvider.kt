package com.baulsupp.cooee.services

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.p.*
import com.baulsupp.cooee.services.strava.StravaProvider
import okhttp3.OkHttpClient

open class CombinedProvider(val stravaProvider: StravaProvider): ProviderFunctions {
  fun init(client: OkHttpClient, clientApi: ClientApi) {
    stravaProvider.init(client, clientApi)
  }

  override suspend fun runCommand(request: CommandRequest): CommandResponse? {
    return stravaProvider.runCommand(request)
  }

  override suspend fun matches(command: String): Boolean {
    return stravaProvider.matches(command)
  }

  override suspend fun suggest(command: CompletionRequest): CompletionResponse? {
    return stravaProvider.suggest(command)
  }

  override suspend fun todo(): TodoResponse? {
    return stravaProvider.todo()
  }
}