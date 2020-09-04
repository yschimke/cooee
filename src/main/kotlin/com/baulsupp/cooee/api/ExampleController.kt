package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandStatus
import com.baulsupp.cooee.p.ImageUrl
import com.squareup.moshi.Moshi
import io.rsocket.util.DefaultPayload
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveFlow
import org.springframework.stereotype.Controller

@Controller("command")
class ExampleController(val moshi: Moshi) {
  @MessageMapping("example")
  suspend fun requestResponse(request: CommandRequest, rSocketRequester: RSocketRequester): CommandResponse {
    println(request)
    val flow = rSocketRequester.route("x").data(CommandRequest("COMMAND")).retrieveFlow<String>()
    println(flow.take(5).toList())
    println(rSocketRequester.rsocket().requestResponse(DefaultPayload.create("PING")).awaitSingle().dataUtf8)
    return CommandResponse(status = CommandStatus.DONE, message = "DONE", image_url = ImageUrl(url = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/Shinz%C5%8D_Abe_Official.jpg/240px-Shinz%C5%8D_Abe_Official.jpg"))
  }
}