package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices
import com.baulsupp.cooee.completion.CommandCompleter
import com.baulsupp.cooee.completion.SimpleCommandCompleter
import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.credentials.TokenSet
import org.slf4j.LoggerFactory

abstract class BaseProvider : Provider {
  val log = LoggerFactory.getLogger(this::class.java)

  var config: MutableMap<String, Any> = mutableMapOf()
  lateinit var appServices: AppServices
  var user: UserEntry? = null

  val userToken
    get() = user?.let { TokenSet(it.email) } ?: DefaultToken

  val client
    get() = appServices.client

  override fun init(appServices: AppServices, user: UserEntry?) {
    this.appServices = appServices
    this.user = user
  }

  override fun configure(config: Map<String, Any>) {
    this.config = config.toMutableMap()
  }

  override fun commandCompleter(): CommandCompleter {
    return SimpleCommandCompleter(name)
  }

  override fun toString(): String {
    return "${this.javaClass.simpleName}:${user?.email ?: "anon"}"
  }

  open fun associatedServices() = setOf<String>()
}
