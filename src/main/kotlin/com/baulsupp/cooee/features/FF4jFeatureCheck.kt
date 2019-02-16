package com.baulsupp.cooee.features

import org.ff4j.FF4j

class FF4jFeatureCheck(val ff4j: FF4j): FeatureCheck {
  override fun enabled(name: String): Boolean {
    return ff4j.check(name)
  }
}
