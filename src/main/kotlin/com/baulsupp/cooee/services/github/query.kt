package com.baulsupp.cooee.services.github

import com.apollographql.apollo.exception.ApolloHttpException
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.LogSeverity
import com.github.cooee.IssueQuery
import com.github.cooee.OpenPullRequestsQuery
import com.github.cooee.ProjectQuery
import com.github.cooee.TopProjectsQuery

suspend fun GithubProvider.projects(): List<TopProjectsQuery.Node> = try {
  graphqlQuery(TopProjectsQuery()).data?.viewer?.topRepositories?.nodes?.mapNotNull {
    it
  }.orEmpty()
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  listOf()
}

suspend fun GithubProvider.pulls(): List<OpenPullRequestsQuery.Node> = try {
    graphqlQuery(OpenPullRequestsQuery()).data?.viewer?.pullRequests?.nodes?.mapNotNull {
      it
    }.orEmpty()
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  listOf()
}

suspend fun GithubProvider.issue(owner: String, project: String, number: Int): IssueQuery.IssueOrPullRequest? = try {
  graphqlQuery(IssueQuery(owner, project, number)).data?.repository?.issueOrPullRequest
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  null
}

suspend fun GithubProvider.project(owner: String, project: String): ProjectQuery.Repository? = try {
  graphqlQuery(ProjectQuery(owner, project)).data?.repository
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  null
}
