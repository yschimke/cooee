package com.baulsupp.cooee.providers.todo

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.users.UserEntry
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TodoProviderTest {
  val appServices = TestAppServices()
  val p = TodoProvider()
  val userEntry = UserEntry("token", "Yuri Schimke", "yuri@coo.ee")

  fun login() {
    runBlocking { p.init(appServices, userEntry) }
  }

  @Test
  fun basic() {
    assertEquals("todo", p.name)
  }

  @Test
  fun todo() = runBlocking {
    login()

    val result = p.todo()

    assertEquals(result.size, 2)
  }

  @Test
  fun todoA() = runBlocking {
    login()

    val createResult = p.go("todo", "test", "todo")

    assertTrue(createResult is Completed)
    assertThat(createResult.message, equalTo("todo.a added"))

    val result = p.go("todo.a")

    assertTrue(result is Completed)
    assertThat(result.message, equalTo("test todo"))
  }

  @Test
  fun basicCommandCompletion() = runBlocking {
    assertThat(
      p.suggest("tod").map { it.line },
      equalTo(listOf("todo.a", "todo.c", "todo"))
    )
  }

  @Test
  fun basicArgumentsCompletion() = runBlocking {
    assertThat(
      p.suggest("todo ").map { it.line },
      equalTo(listOf())
    )
  }
}
