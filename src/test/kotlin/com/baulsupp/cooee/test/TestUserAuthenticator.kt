package com.baulsupp.cooee.test

import com.baulsupp.cooee.users.UserAuthenticator
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultJwtBuilder
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

  fun tokenFor(user: String): String {
    return DefaultJwtBuilder().claim("user", user).compact()
  }
}