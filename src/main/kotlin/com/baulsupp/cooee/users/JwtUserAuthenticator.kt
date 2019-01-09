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

  fun parseToken(token: String?): UserEntry? {
    if (token != null) {
      val jwt = Jwts.parser().setSigningKey("baulsupp4evabaulsupp4evabaulsupp4eva".toByteArray()).parseClaimsJws(token)

      val user = jwt.body["user"] as? String
      val email = jwt.body["email"] as? String

      if (user != null && email != null) {
        return UserEntry(token = token, user = user, email = email)
      }
    }

    return null
  }

  fun tokenFor(user: String): String {
    return DefaultJwtBuilder().claim("user", user).claim("email", "$user@coo.ee")
      .signWith(Keys.hmacShaKeyFor("baulsupp4evabaulsupp4evabaulsupp4eva".toByteArray())).compact()
  }
}

fun main(args: Array<String>) {
  val auth = JwtUserAuthenticator()

  val token1 = auth.tokenFor("yuri")
  println(token1)
  println(auth.parseToken(token1))

  val token =
    "eyJhbGciOiJIUzI1NiIsImtpZCI6IlRCRCIsImV4cCI6MTU0NzA2NjE4M30.eyJlbWFpbCI6Inl1cmlAY29vLmVlIiwidXNlciI6Inl1cmkifQ.LKlqvPDEcgzNUDYP4tbAkZJHVuw5UvH3B5fzneFG3Z8"
  println(auth.parseToken(token))
}
