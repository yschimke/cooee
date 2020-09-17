package com.baulsupp.cooee.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity
import org.springframework.security.config.annotation.rsocket.RSocketSecurity.AuthorizePayloadsSpec
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor
import reactor.core.publisher.Mono.just

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
// https://spring.io/blog/2020/06/17/getting-started-with-rsocket-spring-security
class SecurityConfig {
  @Bean
  fun messageHandler(strategies: RSocketStrategies?): RSocketMessageHandler {
    val mh = RSocketMessageHandler()
    mh.argumentResolverConfigurer.addCustomResolver(AuthenticationPrincipalArgumentResolver())
    mh.rSocketStrategies = strategies!!
    return mh
  }

  @Bean fun userDetailsService(jwtDecoder: ReactiveJwtDecoder): ReactiveUserDetailsService {
//    return JwtUserDetailsService(jwtDecoder)
    return ReactiveUserDetailsService {
      just(User.withUsername("yuri").roles("USER").build())
    }
  }

  @Bean
  fun authorizationToken(rsocket: RSocketSecurity): PayloadSocketAcceptorInterceptor {
    return rsocket
        .authorizePayload { authorize: AuthorizePayloadsSpec ->
          authorize
//              .route("runCommand").authenticated()
              .anyExchange().permitAll()
        }
        .jwt { jwtSpec: RSocketSecurity.JwtSpec ->
          jwtSpec.authenticationManager {
            // TODO security disabled
            it.isAuthenticated = true
            just(it)
          }
        }
        .build()
  }

  @Bean fun jwtDecoder(): ReactiveJwtDecoder {
    return ReactiveJwtDecoder {
      just(Jwt.withTokenValue(it).build())
    }
//    return ReactiveJwtDecoders
//        .fromIssuerLocation("https://uaa.run.pivotal.io/oauth/token")
  }

  @Bean fun reactiveAuthenticationManager(): ReactiveAuthenticationManager {
    return ReactiveAuthenticationManager {
      just(it)
    }
  }
}