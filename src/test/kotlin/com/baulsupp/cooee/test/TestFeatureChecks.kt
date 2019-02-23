package com.baulsupp.cooee.test

import com.baulsupp.cooee.features.FeatureCheck

class TestFeatureChecks: FeatureCheck {
  val checks = mutableMapOf<String, Boolean>()

  override fun enabled(name: String, default: Boolean): Boolean = checks[name] ?: default

  override fun all(): Map<String, Boolean> = checks.toMap()
}
