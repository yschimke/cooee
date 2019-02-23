package com.baulsupp.cooee.features

import org.ff4j.core.FeatureStore
import org.ff4j.core.FlippingExecutionContext
import org.ff4j.strategy.AbstractFlipStrategy

class UserFlipStrategy: AbstractFlipStrategy() {
  override fun evaluate(
    featureName: String?,
    store: FeatureStore?,
    executionContext: FlippingExecutionContext?
  ): Boolean {
    val email = this.initParams["user"]
    return executionContext?.getValue("email", false) == email
  }
}
