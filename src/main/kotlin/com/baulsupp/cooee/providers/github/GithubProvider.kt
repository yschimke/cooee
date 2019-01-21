package com.baulsupp.cooee.providers.github

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.Completion
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryList
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class Repos(val list: List<Repository>)

class GithubProvider : BaseProvider() {
  override val name = "github"

  lateinit var projects: List<Repository>
  lateinit var githubUser: User

  override suspend fun init(appServices: AppServices, user: UserEntry?) {
    super.init(appServices, user)

    coroutineScope {
      val reposAsync = async { listUserRepositories() }

      // TODO reorder when concurrency bug in JVM is fixed
      projects = reposAsync.await()

      val userAsync = async { fetchUser() }

      githubUser = userAsync.await()
    }
  }

  private suspend fun listUserRepositories(): List<Repository> {
    val cachedProjects = appServices.cache.get<Repos>(user?.email, name, "userRepositories")

    return when (cachedProjects) {
      null -> userRepositories()
        .also {
          appServices.cache.set(user?.email, name, "userRepositories", Repos(it))
        }
      else -> cachedProjects.list
    }
  }

  private suspend fun userRepositories(): List<Repository> {
    return appServices.client.queryList("https://api.github.com/user/repos?affiliation=owner,collaborator", userToken)
  }


  private suspend fun fetchUser(): User {
    val cachedUser = appServices.cache.get<User>(user?.email, name, "user")

    return when (cachedUser) {
      null -> user()
        .also {
          appServices.cache.set(user?.email, name, "user", it)
        }
      else -> cachedUser
    }
  }

  private suspend fun user(): User {
    return appServices.client.query("https://api.github.com/user", userToken)
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
    override suspend fun suggestCommands(command: String): List<Completion> {
      return projects.filter { it.full_name.startsWith(command) }
        .map { Completion(it.full_name, name, it.description ?: "Github: ${it.full_name}") }
    }

    override suspend fun matches(command: String): Boolean {
      return projects.any { it.full_name == command }
    }
  }

  override fun associatedServices(): Set<String> = setOf("github")
}
