package com.baulsupp.cooee.api

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class SetupController {
  val logger: Logger = LoggerFactory.getLogger(SetupController::class.java)

  @ConnectMapping
  fun handle(requester: RSocketRequester, @AuthenticationPrincipal jwt: String) {
    logger.info("connected $jwt")
  }
}