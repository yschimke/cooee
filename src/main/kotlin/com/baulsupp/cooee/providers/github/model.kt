package com.baulsupp.cooee.providers.github

import java.time.Instant

data class Owner(
  val avatar_url: String,
  val events_url: String?,
  val followers_url: String?,
  val following_url: String?,
  val gists_url: String?,
  val gravatar_id: String?,
  val html_url: String?,
  val id: Int,
  val login: String,
  val node_id: String?,
  val organizations_url: String?,
  val received_events_url: String?,
  val repos_url: String?,
  val site_admin: Boolean?,
  val starred_url: String?,
  val subscriptions_url: String?,
  val type: String,
  val url: String
)

data class Repository(
  val archive_url: String?,
  val archived: Boolean?,
  val assignees_url: String?,
  val blobs_url: String?,
  val branches_url: String?,
  val clone_url: String?,
  val collaborators_url: String?,
  val comments_url: String?,
  val commits_url: String?,
  val compare_url: String?,
  val contents_url: String?,
  val contributors_url: String?,
  val created_at: String?,
  val default_branch: String?,
  val deployments_url: String?,
  val description: String?,
  val downloads_url: String,
  val events_url: String?,
  val fork: Boolean,
  val forks: Int = 0,
  val forks_count: Int,
  val forks_url: String?,
  val full_name: String,
  val git_commits_url: String?,
  val git_refs_url: String?,
  val git_tags_url: String?,
  val git_url: String,
  val has_downloads: Boolean,
  val has_issues: Boolean,
  val has_pages: Boolean?,
  val has_projects: Boolean?,
  val has_wiki: Boolean,
  val homepage: String?,
  val hooks_url: String?,
  val html_url: String,
  val id: Int = 0,
  val issue_comment_url: String?,
  val issue_events_url: String?,
  val issues_url: String,
  val keys_url: String?,
  val labels_url: String?,
  val language: String?,
  val languages_url: String?,
  val license: License?,
  val merges_url: String,
  val milestones_url: String?,
  val mirror_url: String?,
  val name: String,
  val node_id: String = "",
  val notifications_url: String?,
  val open_issues: Int,
  val open_issues_count: Int,
  val owner: Owner,
  val permissions: Permissions,
  val private: Boolean,
  val pulls_url: String,
  val pushed_at: String?,
  val releases_url: String?,
  val size: Int,
  val ssh_url: String?,
  val stargazers_count: Int,
  val stargazers_url: String?,
  val statuses_url: String?,
  val subscribers_url: String?,
  val subscription_url: String?,
  val svn_url: String?,
  val tags_url: String?,
  val teams_url: String?,
  val trees_url: String?,
  val updated_at: String,
  val url: String,
  val watchers: Int,
  val watchers_count: Int
)

data class Permissions(
  val admin: Boolean?,
  val pull: Boolean?,
  val push: Boolean?
)

data class License(
  val key: String?,
  val name: String,
  val node_id: String?,
  val spdx_id: String?,
  val url: String?
)

data class User(
  val avatar_url: String?,
  val bio: String?,
  val blog: String?,
  val collaborators: Int?,
  val company: String,
  val created_at: String?,
  val disk_usage: Int?,
  val email: String,
  val events_url: String?,
  val followers: Int,
  val followers_url: String?,
  val following: Int,
  val following_url: String?,
  val gists_url: String?,
  val gravatar_id: String?,
  val hireable: Any?,
  val html_url: String?,
  val id: Int,
  val location: String?,
  val login: String,
  val name: String,
  val node_id: String?,
  val organizations_url: String?,
  val owned_private_repos: Int?,
  val plan: Plan?,
  val private_gists: Int?,
  val public_gists: Int?,
  val public_repos: Int?,
  val received_events_url: String?,
  val repos_url: String?,
  val site_admin: Boolean?,
  val starred_url: String?,
  val subscriptions_url: String?,
  val total_private_repos: Int?,
  val two_factor_authentication: Boolean?,
  val type: String,
  val updated_at: String,
  val url: String
)

data class Plan(
  val collaborators: Int?,
  val name: String,
  val private_repos: Int?,
  val space: Int?
)

data class PullRequestResponse(val data: PullRequestData)

data class PullRequests(val nodes: List<PullRequest>)

data class PRRepository(val nameWithOwner: String)

data class PullRequest(
  val createdAt: Instant,
  val reviews: Any,
  val author: Author,
  val state: String,
  val permalink: String,
  val title: String,
  val updatedAt: Instant,
  val number: Int,
  val repository: PRRepository
)

data class Author(
  val name: String,
  val login: String
)

data class Viewer(
  val pullRequests: PullRequests,
  val login: String
)

data class PullRequestData(val viewer: Viewer)

data class Query(val query: String)
