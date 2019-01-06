package com.baulsupp.cooee.providers.jira

data class Project(
  val expand: String,
  val simplified: Boolean,
  val avatarUrls: Map<String, String>,
  val name: String,
  val self: String,
  val style: String,
  val id: String,
  val isPrivate: Boolean = false,
  val projectTypeKey: String,
  val key: String
)

data class Projects(
  val total: Int,
  val isLast: Boolean,
  val maxResults: Int,
  val values: List<Project>,
  val self: String,
  val startAt: Int
)

data class Issue(
  val expand: String,
  val id: String,
  val self: String,
  val key: String,
  val fields: Map<String, Any>
)

data class Issues(
  val total: Int,
  val maxResults: Int,
  val issues: List<Issue>,
  val startAt: Int
)

data class IssueQuery(val jql: String, val fields: List<String>? = null)


