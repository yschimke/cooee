package com.baulsupp.cooee.providers.todo

import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.BaseProvider
import com.baulsupp.cooee.suggester.Suggestion
import com.baulsupp.cooee.suggester.SuggestionType


class TodoProvider : BaseProvider() {
  override val name = "todo"

  data class Todo(val code: String, val message: String, val url: String? = null, val color: String? = null) {
    fun toSuggestion() = Suggestion(
      "todo.$code",
      "todo",
      description = "TODO: $code",
      type = SuggestionType.INFO,
      message = message,
      url = url,
      color = color
    )

    fun toMap(): Map<String, String> {
      return listOf("code" to code, "message" to message, "url" to url, "color" to color).mapNotNull {
        if (it.second != null) {
          it.first to it.second!!
        } else {
          null
        }
      }.toMap()
    }
  }

  val todoCommand = Suggestion("todo", name, description = "Todo Service", type = SuggestionType.COMMAND)

  override suspend fun go(command: String, vararg args: String): GoResult =
    when {
      command == "todo" && args.isEmpty() -> listTodos()
      command == "todo" && args.isNotEmpty() -> createTodo(args.toList())
      else -> findTodo(command)
    }

  private suspend fun listTodos() = Completed(todo().map { "${it.line} ${it.message}" }.joinToString("\n"))

  private suspend fun createTodo(args: List<String>): Completed {
    val todoCodes = todoList().map { it.code }.toSet()

    val code = ('a'..'z').find { !todoCodes.contains(it.toString()) }
    val todo = Todo(code.toString(), args.joinToString(" "))

    val config = todoConfig() + todo.toMap()

    this.config.put("todos", config)

    this.appServices.providerConfigStore.store(this.user!!.email, name, this.config)

    return Completed("todo.$code added")
  }

  override suspend fun matches(command: String): Boolean {
    return command == "todo" || command.startsWith("todo.")
  }

  private suspend fun findTodo(command: String): GoResult {
    return todo().find { it.line == command }?.let {
      if (it.url != null) {
        RedirectResult(it.url)
      } else {
        Completed(it.message ?: "None")
      }
    } ?: Unmatched
  }

  override suspend fun suggest(command: String) = TodoSuggester(this).suggest(command)

  override suspend fun todo(): List<Suggestion> = todoList().map { it.toSuggestion() }

  fun todoList(): List<Todo> {
    return todoConfig().map {
      Todo(
        it.getValue("code"),
        it.getValue("message"),
        it["url"],
        it["color"]
      )
    }
  }

  private fun todoConfig() = (this.config["todos"] as List<Map<String, String>>?).orEmpty()
}
