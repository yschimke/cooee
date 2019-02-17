package com.baulsupp.cooee.features

import com.baulsupp.cooee.users.UserEntry

interface FeatureCheck {
  object ON : FeatureCheck {
    override fun enabled(name: String): Boolean = true
    override fun all(user: UserEntry): Map<String, Boolean> = mapOf()
  }

  fun enabled(name: String): Boolean
  fun isProviderEnabled(name: String): Boolean {
    return enabled("provider.$name")
  }

  fun all(user: UserEntry): Map<String, Boolean>
}
