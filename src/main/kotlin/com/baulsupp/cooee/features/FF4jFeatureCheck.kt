package com.baulsupp.cooee.features

import com.baulsupp.cooee.users.UserEntry
import org.ff4j.FF4j
import org.ff4j.core.FlippingExecutionContext

class FF4jFeatureCheck(val ff4j: FF4j, user: UserEntry) : FeatureCheck {
  val executionContext = FlippingExecutionContext().apply {
    putString("domain", user.email.substringAfter('@'))
    putString("email", user.email)
  }

  override fun enabled(name: String): Boolean {
    return ff4j.check(name, executionContext)
  }

  override fun all(): Map<String, Boolean> = ff4j.features.mapValues { enabled(it.key) }
}
