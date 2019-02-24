package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.completion.ArgumentCompleter
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleArgumentCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.suggester.Suggester
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.credentials.TokenSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BaseProvider : Provider, Suggester {
  @Suppress("LeakingThis")
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  override var config: MutableMap<String, Any> = mutableMapOf()
  lateinit var appServices: AppServices
  var user: UserEntry? = null

  val userToken
    get() = user?.let { TokenSet(it.email) } ?: DefaultToken

  val client
    get() = appServices.client

  override suspend fun init(appServices: AppServices, user: UserEntry?): Unit {
    this.appServices = appServices
    this.user = user
  }

  override fun configure(config: Map<String, Any>) {
    this.config = config.toMutableMap()
  }

  open fun commandCompleter(): CommandCompleter {
    return SimpleCommandCompleter(name, listOf(name))
  }

  open fun argumentCompleter(): ArgumentCompleter = SimpleArgumentCompleter(listOf())

  override suspend fun suggest(command: String): List<Suggestion> {
    return BaseSuggester(name, commandCompleter(), argumentCompleter()).suggest(command)
  }

  override fun toString(): String {
    return "${this.javaClass.simpleName}:${user?.email ?: "anon"}"
  }

  override suspend fun matches(command: String): Boolean {
    val commandName = command.split("\\s+".toRegex())[0]
    return commandName == name
  }

  open fun associatedServices() = setOf<String>()

  fun check(key: String): Boolean = appServices.featureChecks(user).enabled(key, true)
}
