package com.baulsupp.cooee.features

import org.ff4j.FF4j
import org.ff4j.web.FF4jProvider

class CooeeFF4jProvider : FF4jProvider {
  companion object {
    val ff4j = FF4j()
  }

  override fun getFF4j(): FF4j = ff4j
}
