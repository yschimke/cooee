package com.baulsupp.cooee.util

import com.fasterxml.jackson.databind.PropertyNamingStrategy

class WireProto3PropertyNamingStrategy : PropertyNamingStrategy.PropertyNamingStrategyBase() {
  override fun translate(propertyName: String): String {
    if (propertyName.contains('_')) {
      return propertyName.replace(regex) { matchResult ->
        matchResult.groups[1]!!.value.toUpperCase()
      }
    }

    return propertyName
  }

  companion object {
    val regex = "_([a-z])".toRegex()
  }
}