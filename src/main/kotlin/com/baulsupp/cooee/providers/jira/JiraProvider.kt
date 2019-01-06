package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.providers.BaseProvider
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

class JiraProvider : BaseProvider() {
  override val name = "jira"

  override fun associatedServices(): Set<String> = setOf("atlassian")

  // TODO cache with extreme prejudice
  lateinit var knownProjects: List<ProjectReference>

  override suspend fun go(command: String, args: List<String>): GoResult {
    if (command.isProjectOrIssue()) {
      val ipp = issueProjectPair(command)

      if (ipp != null) {
        if (args.firstOrNull() == "vote") {
          ipp.vote()
          return Completed("voted for $command")
        } else if (args.firstOrNull() == "comment") {
          ipp.comment(args.drop(1).joinToString(" "))
          return Completed("comments on $command")
//        } else if (args.firstOrNull() == "close") {
//          ipp.close(args.drop(1).joinToString(" "))
//          return Completed("closed $command")
        } else {
          return RedirectResult(ipp.url(command))
        }
      }
    }

    return Unmatched
  }

  private suspend fun issueProjectPair(command: String): IssueReference? {
    val projectKey = command.projectCode()

    return allprojects().find { projectKey == it.projectKey }?.let { IssueReference(it, command) }
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
    allprojects().forEach { (project, server) ->
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

  suspend fun allprojects(): List<ProjectReference> {
    if (!this::knownProjects.isInitialized) {
      knownProjects =
        instances().flatMap { instance -> projects(instance.id).values.map { ProjectReference(it, instance) } }
    }

    return knownProjects
  }

  suspend fun IssueReference.vote() {
    return appServices.client.query(
      request(
        "https://api.atlassian.com/ex/jira/${project.serverId}/rest/api/3/issue/$issue/votes",
        userToken
      ) {
        postJsonBody("")
      }
    )
  }

  suspend fun IssueReference.comment(comment: String) {
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

  suspend fun IssueReference.close(comment: String) {
    return appServices.client.query(
      request(
        "https://api.atlassian.com/ex/jira/${project.serverId}/rest/api/3/issue/$issue/votes",
        userToken
      ) {
        postJsonBody("")
      }
    )
  }

  private suspend fun mostLikelyProjectIssues(project: String): List<String> =
    issues(project)?.issues?.map { it.key }.orEmpty()

  private fun mostLikelyIssueCompletions(issue: String): List<String> =
    when {
      issue.issueNumber()!!.length > 4 -> listOf(issue)
      else -> listOf(issue) + (0..9).map { issue + it }
    }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      val visibleProjects = allprojects()

      val projectKeys = visibleProjects.map { it.projectKey }

      return when {
        command == "" -> listOf()
        command.isProjectOrPartialProject() -> projectKeys.filter { it.startsWith(command) }.flatMap {
          listOf(
            it,
            "$it-"
          )
        }
        command.isProjectIssueStart() -> mostLikelyProjectIssues(command.projectCode()!!)
        command.isIssueOrPartialIssue() -> mostLikelyIssueCompletions(command)
        else -> listOf()
      }
    }

    override suspend fun matches(command: String): Boolean {
      return (command.isProjectOrIssue()) && knownProjects.any {
        command == it.projectKey || command.startsWith("${it.projectKey}-")
      }
    }
  }

  override fun argumentCompleter(): ArgumentCompleter {
    return SimpleArgumentCompleter(listOf("comment", "vote"))
  }

  // TODO introduce sealed type for JIRA issues to avoid double parsing and null hacks
  private fun String.isProjectOrPartialIssue() = matches("[A-Z]+(?:-\\d*)?".toRegex())

  private fun String.isProjectOrIssue() = matches("[A-Z]+(?:-\\d+)?".toRegex())
  private fun String.isProjectOrPartialProject() = matches("[A-Z]+".toRegex())
  private fun String.isProjectIssueStart() = matches("[A-Z]+-".toRegex())
  private fun String.isIssueOrPartialIssue() = matches("[A-Z]+-\\d+".toRegex())
  private fun String.projectCode(): String? =
    if (isProjectOrPartialIssue()) split('-')[0] else throw NullPointerException(
      "null for $this"
    )

  private fun String.issueNumber(): String? =
    if (isIssueOrPartialIssue()) split('-')[1] else throw NullPointerException(
      "null for $this"
    )
}
