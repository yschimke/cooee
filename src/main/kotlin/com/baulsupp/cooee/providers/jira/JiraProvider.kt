package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.JSON
import com.baulsupp.okurl.kotlin.execute
import com.baulsupp.okurl.kotlin.postJsonBody
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.kotlin.request
import com.baulsupp.okurl.services.atlassian.model.AccessibleResource
import kotlinx.coroutines.*
import okhttp3.RequestBody

data class ProjectReference(val project: Project, val server: AccessibleResource) {
  val projectKey = project.key
  val serverId = server.id
  val url = "https://${server.name}.atlassian.net/browse/$projectKey"
}

data class IssueReference(val project: ProjectReference, val issue: String) {
  fun url(command: String): String {
    return "https://${project.server.name}.atlassian.net/browse/$command"
  }
}

data class KnownInstances(val list: List<AccessibleResource>)
data class KnownProjects(val list: List<ProjectReference>)
data class KnownIssues(val list: List<Issue>)

class JiraProvider : BaseProvider() {
  override val name = "jira"

  override fun associatedServices(): Set<String> = setOf("atlassian")

  lateinit var instances: List<AccessibleResource>
  lateinit var projects: List<ProjectReference>

  val issueFields = listOf("summary", "url")

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    super.init(appServices, user)
    instances = instances()
    projects = listProjects()
    loadKnownIssues(user)
  }

  override suspend fun go(command: String, vararg args: String): GoResult {
    if (command.isProjectOrIssue()) {
      val ipp = issueProjectPair(command)

      if (ipp != null) {
        return when {
          args.firstOrNull() == "vote" -> {
            ipp.vote()
            Completed("voted for $command")
          }
          args.firstOrNull() == "comment" -> {
            ipp.comment(args.drop(1).joinToString(" "))
            Completed("comments on $command")
          }
          else -> RedirectResult(ipp.url(command))
        }
      }
    }

    return Unmatched
  }

  private fun issueProjectPair(command: String): IssueReference? {
    val projectKey = command.projectCode()

    return projects.find { projectKey == it.projectKey }?.let { IssueReference(it, command) }
  }

  private suspend fun instances(): List<AccessibleResource> =
    appServices.cache.get(user?.email, name, "instances") {
      KnownInstances(
        appServices.client.queryList(
          "https://api.atlassian.com/oauth/token/accessible-resources",
          userToken
        )
      )
    }.list

  private suspend fun projects(instanceId: String): Projects {
    return appServices.client.query(
      "https://api.atlassian.com/ex/jira/$instanceId/rest/api/3/project/search",
      userToken
    )
  }

  suspend fun issues(projectKey: String): Issues? {
    projects.forEach { (project, server) ->
      if (project.key == projectKey) {
        return projectIssues(ProjectReference(project, server))
      }
    }

    return null
  }

  suspend fun projectIssues(
    project: ProjectReference
  ): Issues {
    return appServices.cache.get(user?.email, name, "${project.projectKey}.issues") {
      appServices.client.query(
        request(
          "https://api.atlassian.com/ex/jira/${project.serverId}/rest/api/3/search",
          userToken
        ) {
          postJsonBody(IssueQuery("project = ${project.projectKey} order by created DESC", fields = issueFields))
        }
      )
    }
  }

  private suspend fun listProjects(): List<ProjectReference> = appServices.cache.get(user?.email, name, "projects") {
    KnownProjects(instances.flatMap { instance -> projects(instance.id).values.map { ProjectReference(it, instance) } })
  }.list

  private suspend fun loadKnownIssues(user: UserEntry?) {
    if (appServices.featureChecks(user).enabled("backgroundfetch")) {
      GlobalScope.launch {
        projects.map { launch { queryProjectIssues(it) } }
      }
    }
  }

  private suspend fun queryProjectIssues(pr: ProjectReference): List<Issue> =
    appServices.cache.get(user?.email, name, pr.projectKey) {
      KnownIssues(projectIssues(pr).issues)
    }.list

  private suspend fun IssueReference.vote() {
    appServices.client.execute(
      request(
        "https://api.atlassian.com/ex/jira/${project.serverId}/rest/api/3/issue/$issue/votes",
        userToken
      ) {
        postJsonBody("")
      }
    )
  }

  private suspend fun IssueReference.comment(comment: String) {
    appServices.client.execute(
      request(
        "https://api.atlassian.com/ex/jira/${project.serverId}/rest/api/3/issue/$issue/comment",
        userToken
      ) {
        post(
          RequestBody.create(
            JSON,
            "{\"body\":{\"type\":\"doc\",\"version\":1,\"content\":[{\"type\": \"paragraph\",\"content\":[{\"type\": \"text\",\"text\": \"$comment\"}]}]}}"
          )
        )
      }
    )
  }

  suspend fun issue(project: ProjectReference, issueKey: String): Issue =
    appServices.cache.get(user?.email, name, issueKey) {
      appServices.client.query(
        "https://api.atlassian.com/ex/jira/${project.server.id}/rest/api/3/issue/$issueKey?fields=${issueFields.joinToString(
          ","
        )}",
        userToken
      )
    }

  override fun commandCompleter() = TODO()

  override fun argumentCompleter() = TODO()

  override suspend fun matches(command: String): Boolean {
    val parts = command.split("\\s+".toPattern())

    val projectOrIssue = parts.first()

    if (!projectOrIssue.isProjectOrIssue()) {
      return false
    }

    val projectCode = projectOrIssue.projectCode()

    return projects.any { it.projectKey == projectCode }
  }

  override suspend fun suggest(command: String): List<Suggestion> {
    return JiraCommandCompleter(this).suggest(command)
  }
}
