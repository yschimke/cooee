package com.baulsupp.cooee.providers

data class RedirectResult(val location: String?) {
  companion object {
    val UNMATCHED = RedirectResult(null)
  }
}
