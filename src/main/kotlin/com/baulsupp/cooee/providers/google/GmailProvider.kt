package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.okurl.kotlin.query
import com.baulsupp.okurl.kotlin.queryForString

data class Thread(val id: String, val snippet: String, val historyId: String)
data class ThreadList(val threads: List<Thread>, val nextPageToken: String?, val resultSizeEstimate: Int)

class GmailProvider : BaseProvider() {
  override val name = "gmail"

  override suspend fun go(command: String, args: List<String>): GoResult = if (args.isEmpty())
    RedirectResult("https://mail.google.com/")
  else
    listInbox(args)

  private suspend fun listInbox(args: List<String>): GoResult {
    val q = args.first()

    // requires scope = https://www.googleapis.com/auth/cloud-platform,plus.login,plus.profile.emails.read,https://www.googleapis.com/auth/gmail.readonly
    val results = appServices.client.query<ThreadList>("https://www.googleapis.com/gmail/v1/users/me/threads?q=$q")

    return Completed(results.threads.take(5).map { it.snippet.substring(0, 10) }.joinToString("\n"))
  }

  override fun commandCompleter(): CommandCompleter {
    return SimpleCommandCompleter("gmail")
  }

  override fun argumentCompleter() = SimpleArgumentCompleter("label:unread", "label:inbox")

  override fun associatedServices() = setOf("google")
}

