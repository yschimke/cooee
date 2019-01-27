package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.JSON
import com.baulsupp.okurl.kotlin.execute
import com.baulsupp.okurl.kotlin.postJsonBody
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryList
import com.baulsupp.okurl.kotlin.request
import com.baulsupp.okurl.services.atlassian.model.AccessibleResource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.RequestBody
import java.util.concurrent.ConcurrentHashMap

data class ProjectReference(val project: Project, val server: AccessibleResource) {
  val projectKey = project.key
  val serverId = server.id
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
  // TODO cache lazily per project
  var issues: MutableMap<String, List<Issue>> = ConcurrentHashMap()

  val issueFields = listOf("summary", "url")

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    super.init(appServices, user)
    instances = instances()
    projects = listProjects()
    loadKnownIssues()
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
    val cachedInstances = appServices.cache.get<KnownInstances>(user?.email, name, "instances")

    return when (cachedInstances) {
      null -> appServices.client.queryList<AccessibleResource>(
        "https://api.atlassian.com/oauth/token/accessible-resources",
        userToken
      )
        .also {
          appServices.cache.set(user?.email, name, "instances", KnownInstances(it))
        }
      else -> cachedInstances.list
    }
  }

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

  private suspend fun projectIssues(
    project: ProjectReference
  ): Issues {
    return appServices.client.query(
      request(
        "https://api.atlassian.com/ex/jira/${project.serverId}/rest/api/3/search",
        userToken
      ) {
        postJsonBody(IssueQuery("project = ${project.projectKey} order by created DESC", fields = issueFields))
      }
    )
  }

  private suspend fun fetchIssue(
    server: AccessibleResource,
    issueKey: String
  ): Issue? {
    return appServices.client.query(
      "https://api.atlassian.com/ex/jira/${server.id}/rest/api/3/issue/$issueKey?fields=${issueFields.joinToString(",")}",
      userToken
    )
  }

  private suspend fun listProjects(): List<ProjectReference> {
    val cachedProjects = appServices.cache.get<KnownProjects>(user?.email, name, "projects")

    return when (cachedProjects) {
      null -> instances.flatMap { instance -> projects(instance.id).values.map { ProjectReference(it, instance) } }
        .also {
          appServices.cache.set(user?.email, name, "projects", KnownProjects(it))
        }
      else -> cachedProjects.list
    }
  }

  private suspend fun loadKnownIssues() {
    coroutineScope {
      projects.map { async { queryProjectIssues(it) } }.awaitAll()
    }
  }

  private suspend fun queryProjectIssues(pr: ProjectReference): KnownIssues {
    val cachedIssues = appServices.cache.get<KnownIssues>(user?.email, name, pr.projectKey)

    return when (cachedIssues) {
      null -> KnownIssues(projectIssues(pr).issues)
        .also {
          appServices.cache.set(user?.email, name, pr.projectKey, it)
        }
      else -> cachedIssues
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

  suspend fun mostLikelyProjectIssues(project: ProjectReference): List<Suggestion> =
    projectIssues(project).issues.map { issueToCompletion(it) }

  fun issueToCompletion(it: Issue) =
    Suggestion(it.key, description = it.fields["url"].toString(), type = SuggestionType.LINK)

  suspend fun mostLikelyIssueCompletions(issueKey: String): List<Suggestion> {
    val project = projects.find { it.projectKey == issueKey.projectCode() } ?: return listOf()
    val issue = issue(project, issueKey) ?: return listOf()
    val issueCompletion = issueToCompletion(issue)

    return listOf(issueCompletion)
  }

  private suspend fun issue(
    project: ProjectReference,
    issueKey: String
  ) = fetchIssue(project.server, issueKey)

  fun projectCompletion(projectKey: String): Suggestion? =
    projects.find { it.projectKey == projectKey }?.let {
      Suggestion(
        it.projectKey,
        description = "JIRA: " + it.project.name
      )
    }

  override fun commandCompleter() = TODO()

  override fun argumentCompleter() = TODO()

  override suspend fun suggest(command: String): List<Suggestion> {
    return JiraCommandCompleter(this).suggest(command)
  }
}
