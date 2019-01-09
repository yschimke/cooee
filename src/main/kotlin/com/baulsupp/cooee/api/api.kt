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
@Location("/api/v0/command-completion")
data class CommandCompletion(val q: String? = null)

@KtorExperimentalLocationsAPI
@Location("/api/v0/argument-completion")
data class ArgumentCompletion(val q: String? = null) {
  val command: String? = q?.split(" ")?.firstOrNull()
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

data class Completions(val completions: List<String>)

sealed class GoResult {
  override fun toString(): String {
    return this::class.java.simpleName
  }
}

data class RedirectResult(val location: String) : GoResult()
object Unmatched : GoResult()
data class Completed(val message: String, val image: String? = null, val location: String? = null) : GoResult()

data class UserResult(val user: String, val name: String)
