package com.baulsupp.cooee.api

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
@Location("/login")
data class Login(
  val user: String? = null,
  val email: String? = null,
  val secret: String? = null,
  val callback: String? = null
)

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
data class Completed(val message: String) : GoResult()

data class UserResult(val user: String, val name: String)
