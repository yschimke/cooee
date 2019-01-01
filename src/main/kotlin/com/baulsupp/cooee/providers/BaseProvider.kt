package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.credentials.TokenSet
import org.slf4j.LoggerFactory

abstract class BaseProvider : Provider {
  val log = LoggerFactory.getLogger(this::class.java)

  var instance: ProviderInstance? = null
  lateinit var appServices: AppServices

  val userToken
    get() = instance?.let { TokenSet(it.user) } ?: DefaultToken

  override fun init(appServices: AppServices) {
    this.appServices = appServices
  }

  override fun configure(instance: ProviderInstance) {
    this.instance = instance
  }

  override fun toString(): String {
    return "${this.javaClass.simpleName} $instance"
  }
}
