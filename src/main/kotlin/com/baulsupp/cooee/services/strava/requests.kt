package com.baulsupp.cooee.services.strava

import com.baulsupp.cooee.api.ClientApi
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.ImageUrl
import com.baulsupp.okurl.credentials.TokenValue
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.services.mapbox.staticMap
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import com.baulsupp.okurl.services.strava.model.ActivitySummary
import okhttp3.OkHttpClient

private fun Double.format(digits: Int): String = java.lang.String.format("%.${digits}f", this)

suspend fun lastRun(client: OkHttpClient, clientApi: ClientApi): CommandResponse? {
  val tokenString = clientApi.tokenRequest("strava").token ?: return null
  val userToken = TokenValue(StravaAuthInterceptor().serviceDefinition.parseCredentialsString(tokenString))

  val activities =
      client.queryList<ActivitySummary>(
          "https://www.strava.com/api/v3/athlete/activities?page=1&per_page=1",
          userToken
      )

  if (activities.isEmpty()) {
    return CommandResponse("No Activities Found")
  }

  val lastActivity =
      client.query<ActivitySummary>(
          "https://www.strava.com/api/v3/activities/${activities.first().id}",
          userToken
      )

  val map = lastActivity.map?.let {
    staticMap {
      route(it.polyline)
    }
  }
  val url = "https://www.strava.com/activities/${lastActivity.id}"
  val distance = "Distance: ${(lastActivity.distance / 1000.0).format(1)} km"
  val duration = "Duration: ${(lastActivity.elapsed_time / 60.0).format(0)} minutes"

  return CommandResponse(message = "$distance\n$duration", image_url = map?.let { ImageUrl(it) }, location = url)
}
