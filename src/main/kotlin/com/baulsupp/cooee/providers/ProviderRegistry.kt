package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.providers.bookmarks.BookmarksProvider
import com.baulsupp.cooee.providers.circleci.CircleCiProvider
import com.baulsupp.cooee.providers.cooee.CooeeProvider
import com.baulsupp.cooee.providers.github.GithubProvider
import com.baulsupp.cooee.providers.google.GmailProvider
import com.baulsupp.cooee.providers.google.GoogleProvider
import com.baulsupp.cooee.providers.jira.JiraProvider
import com.baulsupp.cooee.providers.mongodb.MongoDbProvider
import com.baulsupp.cooee.providers.opsgenie.GcpProvider
import com.baulsupp.cooee.providers.opsgenie.OpsGenieProvider
import com.baulsupp.cooee.providers.strava.StravaProvider
import com.baulsupp.cooee.providers.trello.TrelloProvider
import com.baulsupp.cooee.providers.twitter.TwitterProvider
import com.baulsupp.cooee.users.UserEntry
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ProviderRegistry(val appServices: AppServices, val registered: Map<String, KClass<out BaseProvider>> = known) {
  suspend fun forUser(user: UserEntry?): CombinedProvider {
    val providers = mutableListOf<BaseProvider>()

    if (user != null) {
      val configs = appServices.providerConfigStore.forUser(user.email)
      val p1 = configs.mapNotNull { pi -> byName(pi.provider, user)?.apply { configure(pi.config) } }
      providers.addAll(p1)
    } else {
      providers.add(BookmarksProvider.loggedOut())
      providers.add(GoogleProvider())
    }

    providers.add(CooeeProvider())

    return CombinedProvider(providers)
  }

  private fun byName(name: String, user: UserEntry): BaseProvider? =
    if (appServices.featureChecks(user).isProviderEnabled(name)) registered[name]?.createInstance() else null

  fun registeredForUser(user: UserEntry): Map<String, BaseProvider> {
    return registered.filterKeys { appServices.featureChecks(user).isProviderEnabled(it) }
      .mapValues { it.value.createInstance() }
  }

  companion object {
    val known = mapOf(
      "cooee" to CooeeProvider::class,
      "google" to GoogleProvider::class,
      "github" to GithubProvider::class,
      "twitter" to TwitterProvider::class,
      "bookmarks" to BookmarksProvider::class,
      "gmail" to GmailProvider::class,
      // disabled for now
      "strava" to StravaProvider::class,
      "trello" to TrelloProvider::class,
      "jira" to JiraProvider::class,
      "opsgenie" to OpsGenieProvider::class,
      "gcp" to GcpProvider::class,
      "mongodb" to MongoDbProvider::class,
      "circleci" to CircleCiProvider::class
    )
  }
}
