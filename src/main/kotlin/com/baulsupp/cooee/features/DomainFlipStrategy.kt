package com.baulsupp.cooee.features

import org.ff4j.core.FeatureStore
import org.ff4j.core.FlippingExecutionContext
import org.ff4j.strategy.AbstractFlipStrategy

class DomainFlipStrategy: AbstractFlipStrategy() {
  override fun evaluate(
    featureName: String?,
    store: FeatureStore?,
    executionContext: FlippingExecutionContext?
  ): Boolean {
    val domain = this.initParams["domain"]
    return executionContext?.getString("domain") == domain
  }
}
