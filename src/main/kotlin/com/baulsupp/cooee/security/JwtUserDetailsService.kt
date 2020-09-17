package com.baulsupp.cooee.security

import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import reactor.core.publisher.Mono

class JwtUserDetailsService(val decoder: ReactiveJwtDecoder) : ReactiveUserDetailsService {
  override fun findByUsername(username: String): Mono<UserDetails> {
    return decoder.decode(username).map {
      User.withUsername("yuri").roles("USER").build()
    }
  }
}