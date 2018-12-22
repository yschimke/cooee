package com.baulsupp.cooee.users

import com.baulsupp.cooee.api.Login
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultJwtBuilder
import io.ktor.application.ApplicationCall
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.request.header
import org.slf4j.LoggerFactory

class JwtUserAuthenticator(val userStore: UserStore) : UserAuthenticator {
  val bearerRegex = "Bearer (.*)".toRegex()

  override suspend fun userForRequest(call: ApplicationCall): String? {
    return call.request.header("Authorization")?.let {
      val token = bearerRegex.matchEntire(it)?.groupValues?.get(1)

      if (token != null) {
        val jwt = Jwts.parser().parseClaimsJwt(token)

        val entry = userStore.userInfo(token)

        // TODO debug

        if (entry != null) {
          return entry.user
        }
      }

      null
    }
  }

  @KtorExperimentalLocationsAPI
  companion object {
    val logger = LoggerFactory.getLogger(JwtUserAuthenticator::class.java)

    fun tokenForLogin(login: Login): String? {
      if (login.user == null) {
        return null
      }

      val builder = DefaultJwtBuilder()

      builder.claim("user", login.user)
      builder.claim("secret", login.secret)
      builder.claim("email", login.email)

      return builder.compact()
    }
  }
}
