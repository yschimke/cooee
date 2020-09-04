package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.*
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.stereotype.Controller

suspend fun tokenRequest(rSocketRequester: RSocketRequester, service: String) =
    rSocketRequester.route("token").data(TokenRequest(service = service)).retrieveAndAwait<TokenResponse>()

@Controller("command")
class CommandController() {
  @MessageMapping("runCommand")
  suspend fun runCommand(request: CommandRequest, rSocketRequester: RSocketRequester): CommandResponse {
    println(tokenRequest(rSocketRequester, "strava"))

    return CommandResponse(status = CommandStatus.DONE, message = "DONE", image_url = ImageUrl(url = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/Shinz%C5%8D_Abe_Official.jpg/240px-Shinz%C5%8D_Abe_Official.jpg"))
  }
}