package com.baulsupp.cooee.providers

abstract class BaseProvider: Provider {
  var instance: ProviderInstance? = null
  var db: ProviderStore? = null

  override fun configure(instance: ProviderInstance, db: ProviderStore) {
    this.instance = instance
    this.db = db
  }

  override fun toString(): String {
    return "${this.javaClass.simpleName} $instance"
  }
}
