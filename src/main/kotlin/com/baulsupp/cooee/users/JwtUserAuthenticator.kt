package com.baulsupp.cooee.users

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultJwtBuilder
import io.jsonwebtoken.security.Keys
import io.ktor.application.ApplicationCall
import io.ktor.request.header

class JwtUserAuthenticator : UserAuthenticator {
  private val bearerRegex = "Bearer (.*)".toRegex()

  override suspend fun userForRequest(call: ApplicationCall): UserEntry? {
    return call.request.header("Authorization")?.let {
      val token = bearerRegex.matchEntire(it)?.groupValues?.get(1)
      return parseToken(token)
    }
  }

  private fun parseToken(token: String?): UserEntry? {
    if (token != null) {
      val jwt = Jwts.parser().setSigningKey("baulsupp4evabaulsupp4evabaulsupp4eva".toByteArray()).parseClaimsJws(token)

      val name = jwt.body["name"] as? String
      val email = jwt.body["email"] as? String

      if (name != null && email != null) {
        return UserEntry(token = token, name = name, email = email)
      }
    }

    return null
  }

  fun tokenFor(user: String): String {
    return DefaultJwtBuilder().claim("name", user).claim("email", "$user@coo.ee")
      .signWith(Keys.hmacShaKeyFor("baulsupp4evabaulsupp4evabaulsupp4eva".toByteArray())).compact()
  }
}
