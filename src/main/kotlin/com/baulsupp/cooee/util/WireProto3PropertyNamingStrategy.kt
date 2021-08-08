package com.baulsupp.cooee.util

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import java.util.*

class WireProto3PropertyNamingStrategy : PropertyNamingStrategy.PropertyNamingStrategyBase() {
  override fun translate(propertyName: String): String {
    if (propertyName.contains('_')) {
      return propertyName.replace(regex) { matchResult ->
          matchResult.groups[1]!!.value.uppercase()
      }
    }

    return propertyName
  }

  companion object {
    val regex = "_([a-z])".toRegex()
  }
}