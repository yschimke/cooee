package com.baulsupp.cooee.users

import com.baulsupp.cooee.api.Login
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultJwtBuilder
import io.ktor.application.ApplicationCall
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.request.header
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class JwtUserAuthenticator(private val userStore: UserStore) : UserAuthenticator {
  private val bearerRegex = "Bearer (.*)".toRegex()

  override suspend fun userForRequest(call: ApplicationCall): String? {
    return call.request.header("Authorization")?.let {
      val token = bearerRegex.matchEntire(it)?.groupValues?.get(1)

      if (token != null) {
        val jwt = Jwts.parser().parseClaimsJwt(token)

        // TODO use token and validate signature
        val user = jwt.body["user"] as? String

        if (user != null) {
          val entry = userStore.userInfo(user)

          if (entry != null) {
            return entry.user
          }
        }
      }

      null
    }
  }

  @KtorExperimentalLocationsAPI
  companion object {
    val logger: Logger = LoggerFactory.getLogger(JwtUserAuthenticator::class.java)

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
