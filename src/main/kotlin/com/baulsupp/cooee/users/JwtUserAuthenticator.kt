package com.baulsupp.cooee.users

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultJwtBuilder
import io.jsonwebtoken.security.Keys
import io.ktor.application.ApplicationCall
import io.ktor.request.header

class JwtUserAuthenticator : UserAuthenticator {

  override suspend fun userForRequest(call: ApplicationCall): UserEntry? {
    return call.request.header("Authorization")?.let {
      return parseHeader(it)
    }
  }

  companion object {
    val code = "baulsupp4evabaulsupp4evabaulsupp4eva"

    private val bearerRegex = "Bearer (.*)".toRegex()

    fun parseHeader(it: String): UserEntry? {
      val token = bearerRegex.matchEntire(it)?.groupValues?.get(1)
      return token?.let { parseToken(token) }
    }

    fun parseToken(token: String): UserEntry? {
      val jwt = Jwts.parser().setSigningKey(code.toByteArray()).parseClaimsJws(token)

      val name = jwt.body["name"] as? String
      val email = jwt.body["email"] as? String

      if (name != null && email != null) {
        return UserEntry(token = token, name = name, email = email)
      }

      return null
    }

    fun tokenFor(user: String): String {
      return DefaultJwtBuilder().claim("name", user).claim("email", "$user@coo.ee")
        .signWith(Keys.hmacShaKeyFor(code.toByteArray())).compact()
    }
  }
}
