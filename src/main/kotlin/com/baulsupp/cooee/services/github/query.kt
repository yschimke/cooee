package com.baulsupp.cooee.services.github

import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.LogSeverity
import com.baulsupp.okurl.kotlin.postJsonBody
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.request
import com.baulsupp.okurl.util.ClientException

data class Repos(val list: List<Repository>)

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

suspend fun GithubProvider.recentActivePullRequests(): List<PullRequest> =
    cache.get(token(), name, "pullRequestResponse") {
      client.query<PullRequestResponse>(request {
        url("https://api.github.com/graphql")
        postJsonBody(Query(openPullRequests))
      })
    }.data.viewer.pullRequests.nodes

suspend fun GithubProvider.listUserRepositories(): List<Repository> =
    cache.get(token(), name, "userRepositories") {
      Repos(
          queryUserRepos()
      )
    }.list

suspend fun GithubProvider.listStarredRepositories(): List<Repository> =
    cache.get(token(), name, "starredRepositories") {
      Repos(
          queryStarredRepos()
      )
    }.list

suspend fun GithubProvider.projects(): List<Repository> {
  return try {
    (listUserRepositories() + listStarredRepositories()).distinctBy { it.url }
  } catch (ce: ClientException) {
    if (ce.code == 401) {
      clientApi.logToClient(LogRequest(message = "Unauthorized to github", severity = LogSeverity.WARN))
      listOf()
    } else {
      throw ce
    }
  }
}

suspend fun GithubProvider.queryStarredRepos(): List<Repository> {
  return client.queryGithubPages<Repository>(
      "https://api.github.com/user/starred?per_page=25",
      tokenSet = token()
  ).filter { it.archived == false }
}

suspend fun GithubProvider.queryUserRepos(): List<Repository> {
  return client.queryGithubPages<Repository>(
      "https://api.github.com/user/repos?per_page=25",
      tokenSet = token()
  ).filter { it.archived == false }
}

suspend fun GithubProvider.fetchUser(): User = cache.get(token(), name, "user") {
  client.query("https://api.github.com/user", token())
}
