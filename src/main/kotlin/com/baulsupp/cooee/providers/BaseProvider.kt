package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BaseProvider : Provider {
  val log by lazy {
    LoggerFactory.getLogger(this::class.java)
  }

  var instance: ProviderInstance? = null
  lateinit var appServices: AppServices

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
