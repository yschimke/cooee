package com.baulsupp.cooee.providers.twitter

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.providers.BaseProvider
import okhttp3.OkHttpClient

class TwitterProvider(client: OkHttpClient) : BaseProvider() {
  override val name = "twitter"

  override suspend fun go(command: String, args: List<String>): GoResult {
    val text = if (args.isNotEmpty()) "&text=" + args.joinToString(" ") else ""

    // TODO lookup real user
    val userid = 735627895645691905

    return RedirectResult("https://m.twitter.com/messages/compose?recipient_id=$userid$text")
  }

  override fun commandCompleter(): CommandCompleter = object : CommandCompleter {
    override suspend fun suggestCommands(command: String): List<String> {
      // TODO
      return listOf("@tgmcclen", "@yschimke", "@dmorentin", "@shoutcooee").filter { it.startsWith(command) }
    }

    override suspend fun matches(command: String): Boolean {
      // TODO exact match twitter username pattern
      return command.startsWith("@") && command.length > 1
    }
  }
}
