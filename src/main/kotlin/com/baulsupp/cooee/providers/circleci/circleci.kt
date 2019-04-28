package com.baulsupp.cooee.providers.circleci

data class User(
  val vcsType: String?,
  val avatarUrl: String?,
  val name: String?,
  val id: Int?,
  val login: String?,
  val isUser: Boolean?
)

data class Workflows(
  val workspaceId: String?,
  val workflowId: String?,
  val jobName: String?,
  val workflowName: String?,
  val jobId: String?,
  val upstreamConcurrencyMap: Any?
)

data class Build(
  val authorName: String?,
  val fleet: String,
  val stopTime: String?,
  val subject: String?,
  val vcsUrl: String?,
  val why: String?,
  val committerDate: String?,
  val workflows: Workflows,
  val body: String?,
  val branch: String,
  val platform: String,
  val lifecycle: String,
  val vcsTag: Any?,
  val parallel: Int?,
  val authorDate: String?,
  val authorEmail: String?,
  val buildUrl: String?,
  val outcome: String?,
  val committerEmail: String?,
  val queuedAt: String?,
  val buildTimeMillis: Int?,
  val dontBuild: Any?,
  val committerName: String?,
  val startTime: String?,
  val reponame: String,
  val buildNum: Int?,
  val usageQueuedAt: String?,
  val user: User,
  val username: String,
  val vcsRevision: String?,
  val status: String
)
