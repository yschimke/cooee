package com.baulsupp.cooee.services.github

import com.github.cooee.OpenPullRequestsQuery
import com.github.cooee.TopProjectsQuery

suspend fun GithubProvider.projects(): List<TopProjectsQuery.Node> =
    graphqlQuery(TopProjectsQuery()).data?.viewer?.topRepositories?.nodes?.mapNotNull {
      it
    }.orEmpty()

suspend fun GithubProvider.pulls(): List<OpenPullRequestsQuery.Node> =
    graphqlQuery(OpenPullRequestsQuery()).data?.viewer?.pullRequests?.nodes?.mapNotNull {
      it
    }.orEmpty()
