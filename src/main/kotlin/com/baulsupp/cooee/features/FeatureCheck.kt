package com.baulsupp.cooee.features

interface FeatureCheck {
  object ON : FeatureCheck {
    override fun enabled(name: String): Boolean = true
  }

  fun enabled(name: String): Boolean
  fun isProviderEnabled(name: String): Boolean {
    return enabled("provider.$name")
  }
}
