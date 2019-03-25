package com.baulsupp.cooee.providers.github

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.postJsonBody
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.request
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class Repos(val list: List<Repository>)

class GithubProvider : BaseProvider() {
  override val name = "github"

  val openPullRequests = """
    query {
      viewer {
        login
        pullRequests(first: 20, states:OPEN, orderBy: {field: UPDATED_AT, direction: DESC}) {
          nodes {
            number
            permalink
            state
            title
            createdAt
            updatedAt
            repository {
              nameWithOwner
            }
            author {
              login
              ... on User {
                name
              }
            }
            reviews(first: 20, states: [APPROVED, CHANGES_REQUESTED]) {
              nodes {
                state
              }
            }
          }
        }
      }
    }
  """.trimIndent()

  lateinit var projects: List<Repository>
  lateinit var githubUser: User

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    super.init(appServices, user)

    coroutineScope {
      val reposAsync = async {
        (listUserRepositories() + listStarredRepositories()).distinctBy { it.url }
      }

      // TODO reorder when concurrency bug in JVM is fixed
      projects = reposAsync.await()

      val userAsync = async { fetchUser() }

      githubUser = userAsync.await()
    }
  }

  private suspend fun listUserRepositories(): List<Repository> =
    appServices.cache.get(user?.email, name, "userRepositories") {
      Repos(
        queryUserRepos()
      )
    }.list

  private suspend fun listStarredRepositories(): List<Repository> =
    appServices.cache.get(user?.email, name, "starredRepositories") {
      Repos(
        queryStarredRepos()
      )
    }.list

  private suspend fun queryStarredRepos(): List<Repository> {
    return appServices.client.queryGithubPages<Repository>(
      "https://api.github.com/user/starred?per_page=25",
      tokenSet = userToken
    ).filter { it.archived == false }
  }

  private suspend fun queryUserRepos(): List<Repository> {
    return appServices.client.queryGithubPages<Repository>(
      "https://api.github.com/user/repos?per_page=25",
      tokenSet = userToken
    ).filter { it.archived == false }
  }

  private suspend fun fetchUser(): User = appServices.cache.get(user?.email, name, "user") {
    appServices.client.query("https://api.github.com/user", userToken)
  }

  override suspend fun go(command: String, vararg args: String): GoResult {
    val r = "(\\w+)/(\\w+)(?:#(\\d+))?".toRegex()

    val result = r.matchEntire(command)

    if (result != null) {
      val (org, project, id) = result.destructured

      return if (id.isEmpty()) {
        RedirectResult("https://github.com/$org/$project")
      } else {
        RedirectResult("https://github.com/$org/$project/issues/$id")
      }
    }

    return Unmatched
  }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<Suggestion> {
      return projects
        .map {
          Suggestion(
            it.full_name,
            provider = name,
            description = it.description ?: "Github: ${it.full_name}",
            type = SuggestionType.LINK,
            url = "https://github.com/${it.full_name}"
          )
        } + Suggestion(
        "github",
        provider = name,
        description = "Github",
        type = SuggestionType.LINK,
        url = "https://github.com"
      )
    }

    override suspend fun matches(command: String): Boolean {
      return this@GithubProvider.matches(command)
    }
  }

  override fun associatedServices(): Set<String> = setOf("github")

  override suspend fun matches(command: String): Boolean {
    return projects.any { it.full_name == command } || command == "github"
  }

  private suspend fun recentActivePullRequests(): List<PullRequest> =
    appServices.cache.get(user?.email, name, "pullRequests") {
      query<PullRequestResponse>(request {
        url("https://api.github.com/graphql")
//        header("Accept", "application/vnd.github.antiope-preview")
        postJsonBody(Query(openPullRequests))
      }).data.viewer.pullRequests.nodes
    }

  override suspend fun todo(): List<Suggestion> {
    return recentActivePullRequests().map {
      Suggestion(
        "${it.repository.nameWithOwner}#${it.number}", name, it.title, SuggestionType.LINK,
        url = it.permalink
      )
    }
  }
}
