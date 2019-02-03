package com.baulsupp.cooee.providers.google

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.okurl.kotlin.query

class GmailProvider : BaseProvider() {
  override val name = "gmail"

  override suspend fun go(command: String, vararg args: String): GoResult = if (args.isEmpty())
    RedirectResult("https://mail.google.com/")
  else
    listInbox(args.toList())

  private suspend fun listInbox(args: List<String>): GoResult {
    val q = args.first()

    // requires scope = https://www.googleapis.com/auth/cloud-platform,plus.login,plus.profile.emails.read,https://www.googleapis.com/auth/gmail.readonly
    val results = appServices.client.query<ThreadList>("https://www.googleapis.com/gmail/v1/users/me/threads?q=$q")

    return Completed(results.threads.orEmpty().take(5).joinToString("\n") { it.snippet.substring(0, 10) })
  }

  override fun commandCompleter(): CommandCompleter {
    return SimpleCommandCompleter(name, listOf("gmail"))
  }

  override fun argumentCompleter() = SimpleArgumentCompleter(listOf("label:unread", "label:inbox"))

  override fun associatedServices() = setOf("google")
}
