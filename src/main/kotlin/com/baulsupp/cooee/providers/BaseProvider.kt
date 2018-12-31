package com.baulsupp.cooee.providers

import com.baulsupp.cooee.AppServices

abstract class BaseProvider : Provider {
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
