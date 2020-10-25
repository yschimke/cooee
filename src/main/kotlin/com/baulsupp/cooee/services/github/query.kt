package com.baulsupp.cooee.services.github

import com.apollographql.apollo.exception.ApolloHttpException
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.LogSeverity
import com.github.cooee.AddCommentMutation
import com.github.cooee.IssueQuery
import com.github.cooee.OpenPullRequestsQuery
import com.github.cooee.ProjectQuery
import com.github.cooee.ProjectReleasesQuery
import com.github.cooee.TopProjectsQuery

suspend fun GithubProvider.projects(): List<TopProjectsQuery.Node> = try {
  apolloClient.graphqlQuery(TopProjectsQuery(),
      token()).data?.viewer?.topRepositories?.nodes?.mapNotNull {
    it
  }.orEmpty()
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  listOf()
}

suspend fun GithubProvider.projectsReleases(
  owner: String,
  project: String
): List<ProjectReleasesQuery.Node> = try {
  apolloClient.graphqlQuery(ProjectReleasesQuery(owner, project), token())
      .data?.repository?.releases?.nodes?.mapNotNull {
    it
  }.orEmpty()
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  listOf()
}

suspend fun GithubProvider.pulls(): List<OpenPullRequestsQuery.Node> = try {
  apolloClient.graphqlQuery(OpenPullRequestsQuery(),
      token()).data?.viewer?.pullRequests?.nodes?.mapNotNull {
    it
  }.orEmpty()
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  listOf()
}

suspend fun GithubProvider.issue(
  owner: String,
  project: String,
  number: Int
): IssueQuery.IssueOrPullRequest? = try {
  apolloClient.graphqlQuery(IssueQuery(owner, project, number),
      token()).data?.repository?.issueOrPullRequest
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  null
}

suspend fun GithubProvider.comment(
  owner: String,
  project: String,
  number: Int,
  comment: String
): Boolean {
  return try {
    val item = issue(owner, project, number)
    val subjectId = item?.asIssue?.id ?: item?.asPullRequest?.id ?: return false
    apolloClient.graphqlMutation(AddCommentMutation(subjectId, comment), token())
    true
  } catch (ahe: ApolloHttpException) {
    clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
    false
  }
}

suspend fun GithubProvider.project(owner: String, project: String): ProjectQuery.Repository? = try {
  apolloClient.graphqlQuery(ProjectQuery(owner, project), token()).data?.repository
} catch (ahe: ApolloHttpException) {
  clientApi.logToClient(LogRequest(message = "failed $ahe", severity = LogSeverity.WARN))
  null
}
