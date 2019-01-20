package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.JSON
import com.baulsupp.okurl.kotlin.execute
import com.baulsupp.okurl.kotlin.postJsonBody
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.kotlin.request
import com.baulsupp.okurl.services.atlassian.model.AccessibleResource
import okhttp3.RequestBody

data class ProjectReference(val project: Project, val server: AccessibleResource) {
  val projectKey = project.key
  val serverId = server.id
}

data class IssueReference(val project: ProjectReference, val issue: String) {
  fun url(command: String): String {
    return "https://${project.server.name}.atlassian.net/browse/$command"
  }
}

data class KnownProjects(val list: List<ProjectReference>)

class JiraProvider : BaseProvider() {
  override val name = "jira"

  override fun associatedServices(): Set<String> = setOf("atlassian")

  lateinit var projects: List<ProjectReference>

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    super.init(appServices, user)
    projects = listProjects()
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

  private suspend fun instances(): List<AccessibleResource> {
    return appServices.client.queryList("https://api.atlassian.com/oauth/token/accessible-resources", userToken)
  }

  private suspend fun projects(instanceId: String): Projects {
    return appServices.client.query(
      "https://api.atlassian.com/ex/jira/$instanceId/rest/api/3/project/search",
      userToken
    )
  }

  private suspend fun issues(projectKey: String): Issues? {
    projects.forEach { (project, server) ->
      if (project.key == projectKey) {
        return appServices.client.query(
          request(
            "https://api.atlassian.com/ex/jira/${server.id}/rest/api/3/search",
            userToken
          ) {
            postJsonBody(IssueQuery("project = $projectKey order by created DESC", fields = listOf("summary")))
          }
        )
      }
    }

    return null
  }

  private suspend fun listProjects(): List<ProjectReference> {
    val cachedProjects = appServices.cache.get<KnownProjects>(user?.email, name, "projects")

    return when (cachedProjects) {
      null -> instances().flatMap { instance -> projects(instance.id).values.map { ProjectReference(it, instance) } }
        .also {
          appServices.cache.set(user?.email, name, "projects", KnownProjects(it))
        }
      else -> cachedProjects.list
    }
  }

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

  suspend fun mostLikelyProjectIssues(project: String): List<String> =
    issues(project)?.issues?.map { it.key }.orEmpty()

  fun mostLikelyIssueCompletions(issue: String): List<String> =
    when {
      issue.issueNumber()!!.length > 4 -> listOf(issue)
      else -> listOf(issue) + (0..9).map { issue + it }
    }

  override fun commandCompleter() = JiraCommandCompleter(this)

  override fun argumentCompleter() = JiraArgumentCompleter(this)
}
