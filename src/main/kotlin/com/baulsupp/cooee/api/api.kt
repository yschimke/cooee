package com.baulsupp.cooee.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/go")
data class Go(val q: String? = null) {
  val command: String? = q?.split(" ")?.firstOrNull()
  val args: List<String> = q?.split(" ")?.drop(1).orEmpty()
}

@KtorExperimentalLocationsAPI
@Location("/api/v0/goinfo")
data class GoInfo(val q: String? = null) {
  val command: String? = q?.split(" ")?.firstOrNull()
  val args: List<String> = q?.split(" ")?.drop(1).orEmpty()
}

@KtorExperimentalLocationsAPI
@Location("/api/v0/user")
class UserInfo

@KtorExperimentalLocationsAPI
@Location("/api/v0/completion")
data class CompletionRequest(val q: String? = "") {
  fun isCommand(): Boolean = args.isEmpty()

  val command: String = q?.split(" ")?.firstOrNull() ?: ""
  val args: List<String> = q?.split(" ")?.drop(1).orEmpty()
}

@KtorExperimentalLocationsAPI
@Location("/api/v0/search-suggestion")
data class SearchSuggestion(val q: String? = null)

data class SearchSuggestionsResults(
  val q: String,
  val suggestions: List<String>,
  val descriptions: List<String>,
  val links: List<String>
)

class SearchSuggestionsResultsAdapter : JsonAdapter<SearchSuggestionsResults>() {
  override fun fromJson(reader: JsonReader): SearchSuggestionsResults? = TODO()

  override fun toJson(writer: JsonWriter, value: SearchSuggestionsResults?) {
    if (value != null) {
      writer.beginArray()
      writer.value(value.q)
      writer.beginArray()
      value.suggestions.forEach {
        writer.value(it)
      }
      writer.endArray()
      writer.beginArray()
      value.descriptions.forEach {
        writer.value(it)
      }
      writer.endArray()
      writer.beginArray()
      value.links.forEach {
        writer.value(it)
      }
      writer.endArray()
      writer.endArray()
    }
  }
}

@KtorExperimentalLocationsAPI
@Location("/api/v0/authorize")
data class Authorize(
  val serviceName: String? = null,
  val token: String? = null,
  val tokenSet: String? = null
)

data class CompletionItem(val word: String, val line: String, val description: String)

@KtorExperimentalLocationsAPI
data class Completions(val completions: List<CompletionItem>) {
  companion object {
    fun complete(command: CompletionRequest, commands: List<String>): Completions {
      val query = command.q ?: ""

      // TODO improve or test logic
      val prefix = query.substring(0, query.lastIndexOf(" ") + 1)

      return Completions(commands.map { s -> CompletionItem(s, prefix + s, "Description for '$s'") })
    }
  }
}

sealed class GoResult {
  override fun toString(): String {
    return this::class.java.simpleName
  }
}

data class RedirectResult(val location: String) : GoResult()
object Unmatched : GoResult()
data class Completed(val message: String, val image: String? = null, val location: String? = null) : GoResult()

data class UserResult(val user: String, val name: String)
