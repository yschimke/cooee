package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.Completion
import com.baulsupp.cooee.completion.Completion.Companion.completions

class JiraArgumentCompleter(val provider: JiraProvider) : ArgumentCompleter {
  override suspend fun suggestArguments(command: String, arguments: List<String>?): List<Completion> {
    return completions("vote", "comment")
  }
}
