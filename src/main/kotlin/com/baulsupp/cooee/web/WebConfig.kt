package com.baulsupp.cooee.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebConfig: WebFluxConfigurer {
  @Bean fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
    http.authorizeExchange().anyExchange().permitAll()
    return http.build()
  }
}