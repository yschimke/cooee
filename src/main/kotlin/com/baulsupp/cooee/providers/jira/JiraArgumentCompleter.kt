package com.baulsupp.cooee.providers.jira

import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.Suggestion.Companion.completions

class JiraArgumentCompleter(val provider: JiraProvider) : ArgumentCompleter {
  override suspend fun suggestArguments(command: String, arguments: List<String>?): List<String> {
    return listOf("vote", "comment")
  }
}
