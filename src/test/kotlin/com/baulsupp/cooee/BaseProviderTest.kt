package com.baulsupp.cooee

import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.users.UserEntry
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class BaseProviderTest<P : BaseProvider>(val type: KClass<P>) {
  val userEntry = UserEntry("token", "yuri", "yuri@coo.ee")

  val p = type.createInstance().apply {
    runBlocking { init(testAppServices(), userEntry) }
  }

  open fun testAppServices(): TestAppServices = TestAppServices()

  @Test
  fun basicInfo() {
    assertNotNull(p.name)
  }

  @Test
  fun commandCompletion() = runBlocking {
    assertThat(
      p.suggest(p.name.dropLast(1)).map { it.line },
      equalTo(listOf(p.name))
    )
  }

  @Test
  fun matches() = runBlocking {
    assertTrue(p.matches(p.name))
  }
}
