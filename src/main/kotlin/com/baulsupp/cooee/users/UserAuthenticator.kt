package com.baulsupp.cooee.users

import io.ktor.application.ApplicationCall

interface UserAuthenticator {
  suspend fun userForRequest(call: ApplicationCall): UserEntry?
}
