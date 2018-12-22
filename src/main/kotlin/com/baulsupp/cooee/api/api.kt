package com.baulsupp.cooee.api

import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location

@KtorExperimentalLocationsAPI
@Location("/go")
data class Go(val q: String? = null) {
  val command: String? = q?.split(" ")?.firstOrNull()
  val args: List<String> = q?.split(" ")?.drop(1).orEmpty()
}

@Location("/api/v0/goinfo")
data class GoInfo(val q: String? = null) {
  val command: String? = q?.split(" ")?.firstOrNull()
  val args: List<String> = q?.split(" ")?.drop(1).orEmpty()
}

@Location("/api/v0/user")
class UserInfo {}

@Location("/api/v0/command-completion")
data class CommandCompletion(val q: String? = null)

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

data class Completions(val completions: List<String>)

sealed class GoResult

data class RedirectResult(val location: String) : GoResult()
object Unmatched : GoResult()
object Completed : GoResult()

data class UserResult(val user: String, val name: String)
