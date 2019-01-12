package com.baulsupp.cooee.providers.strava

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.services.mapbox.staticMap
import com.baulsupp.okurl.services.strava.model.ActivitySummary

class StravaProvider : BaseProvider() {
  override val name = "strava"

  fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

  override fun associatedServices(): Set<String> = setOf("strava")

  override suspend fun go(command: String, vararg args: String): GoResult = if (args.firstOrNull() == "lastrun") {
    lastRun()
  } else {
    Unmatched
  }

  private suspend fun lastRun(): GoResult {
    val activities =
      appServices.client.queryList<ActivitySummary>("https://www.strava.com/api/v3/athlete/activities?page=1&per_page=1", userToken)

    if (activities.isEmpty()) {
      return Completed("No Activities Found")
    }

    val lastActivity =
      appServices.client.query<ActivitySummary>("https://www.strava.com/api/v3/activities/${activities.first().id}", userToken)

    val map = staticMap {
      route(lastActivity.map.polyline)
    }
    val url = "https://www.strava.com/activities/${lastActivity.id}"
    val distance = "Distance: ${(lastActivity.distance / 1000.0).format(1)} km"
    val duration = "Duration: ${(lastActivity.elapsed_time / 60.0).format(0)} minutes"

    return Completed(message = "$distance\n$duration", image = map, location = url)
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return SimpleArgumentCompleter(listOf("lastrun"))
  }
}
