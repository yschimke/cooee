package com.baulsupp.cooee.providers.jira

// TODO introduce sealed type for JIRA issues to avoid double parsing and null hacks
fun String.isProjectOrPartialIssue() = matches("[A-Z]+(?:-\\d*)?".toRegex())

fun String.isProjectOrIssue() = matches("[A-Z]+(?:-\\d+)?".toRegex())
fun String.isProjectOrPartialProject() = matches("[A-Z]+".toRegex())
fun String.isProjectIssueStart() = matches("[A-Z]+-".toRegex())
fun String.isIssueOrPartialIssue() = matches("[A-Z]+-\\d+".toRegex())
fun String.projectCode(): String? =
  if (isProjectOrPartialIssue()) split('-')[0] else throw NullPointerException(
    "null for $this"
  )

fun String.issueNumber(): String? =
  if (isIssueOrPartialIssue()) split('-')[1] else throw NullPointerException(
    "null for $this"
  )
