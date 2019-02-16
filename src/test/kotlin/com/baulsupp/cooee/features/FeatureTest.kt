package com.baulsupp.cooee.features

import com.baulsupp.cooee.test.TestAppServices
import org.junit.Test
import kotlin.test.assertEquals

class FeatureTest {
  @Test
  fun testX() {
    val features = TestAppServices().featureSwitches
    assertEquals(false, features.check("fake"))
  }
}
