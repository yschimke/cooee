package com.baulsupp.cooee.providers.circleci

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.providers.trello.BoardResponse
import com.baulsupp.cooee.providers.trello.Boards
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType
import com.baulsupp.okurl.kotlin.queryList

data class RecentBuilds(val builds: List<Build>)

class CircleCiProvider : BaseProvider() {
  override val name = "circleci"

  val circleciCommand = Suggestion(
    "circleci",
    name,
    "CircleCI",
    type = SuggestionType.LINK,
    url = "https://circleci.com"
  )

  override fun associatedServices(): Set<String> = setOf("circleci")

  override suspend fun go(command: String, vararg args: String): GoResult = when {
    args.isEmpty() -> RedirectResult("https://circleci.com/dashboard")
    else -> Unmatched
  }

  override suspend fun matches(command: String): Boolean {
    return command == name
  }

  override suspend fun suggest(command: String): List<Suggestion> {
    return CircleCiSuggester(this).suggest(command)
  }

  suspend fun readRecentBuilds(): List<Build> = appServices.cache.get(user?.email, name, "builds") {
    RecentBuilds(client.queryList("https://circleci.com/api/v1.1/recent-builds?limit=100&shallow=true", userToken))
  }.builds
}
