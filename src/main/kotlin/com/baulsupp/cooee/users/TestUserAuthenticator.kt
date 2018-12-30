package com.baulsupp.cooee.users

import io.jsonwebtoken.Jwts
import io.ktor.application.ApplicationCall
import io.ktor.request.header

class TestUserAuthenticator : UserAuthenticator {
  private val bearerRegex = "Bearer (.*)".toRegex()

  override suspend fun userForRequest(call: ApplicationCall): String? {
    return call.request.header("Authorization")?.let {
      val token = bearerRegex.matchEntire(it)?.groupValues?.get(1)

      val jwt = Jwts.parser().parseClaimsJwt(token)

      jwt.body["user"] as? String
    }
  }
}
