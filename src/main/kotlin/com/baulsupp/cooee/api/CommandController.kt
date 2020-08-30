package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandStatus
import com.baulsupp.cooee.p.ImageUrl
import com.squareup.moshi.Moshi
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller("command")
class CommandController(val moshi: Moshi) {
  @MessageMapping("runCommand")
  suspend fun requestResponse(request: String): String {
    val requestCommand = moshi.adapter(CommandRequest::class.java).fromJson(request)!!
    val response = executeRequest(requestCommand)
    return moshi.adapter(CommandResponse::class.java).toJson(response)
  }

  private fun executeRequest(requestCommand: CommandRequest) =
      CommandResponse(status = CommandStatus.DONE, message = "DONE", image_url = ImageUrl(url = "https://pbs.twimg.com/card_img/1298521627341332480/0BnXd4n8?format=jpg&name=small"))
}