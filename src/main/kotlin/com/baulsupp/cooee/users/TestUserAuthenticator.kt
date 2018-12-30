package com.baulsupp.cooee.users

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultJwtBuilder
import io.ktor.application.ApplicationCall
import io.ktor.request.header

class TestUserAuthenticator() : UserAuthenticator {
  val bearerRegex = "Bearer (.*)".toRegex()

  override suspend fun userForRequest(call: ApplicationCall): String? {
    return call.request.header("Authorization")?.let {
      val token = bearerRegex.matchEntire(it)?.groupValues?.get(1)

      var jwt = Jwts.parser().parseClaimsJwt(token)

      jwt.body["user"] as? String
    }
  }

  fun tokenFor(user: String): String {
    return DefaultJwtBuilder().claim("user", user).compact()
  }
}
