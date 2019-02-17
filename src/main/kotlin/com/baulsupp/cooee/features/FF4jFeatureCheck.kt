package com.baulsupp.cooee.features

import com.baulsupp.cooee.users.UserEntry
import org.ff4j.FF4j

class FF4jFeatureCheck(val ff4j: FF4j) : FeatureCheck {
  override fun enabled(name: String): Boolean = ff4j.check(name)

  override fun all(user: UserEntry): Map<String, Boolean> = ff4j.features.mapValues { enabled(it.key) }
}
