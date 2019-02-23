package com.baulsupp.cooee.features

interface FeatureCheck {
  object ON : FeatureCheck {
    override fun enabled(name: String, default: Boolean): Boolean = true
    override fun all(): Map<String, Boolean> = mapOf()
  }

  fun enabled(name: String, default: Boolean = false): Boolean
  fun isProviderEnabled(name: String): Boolean {
    return enabled("provider.$name", true)
  }

  fun all(): Map<String, Boolean>
}
