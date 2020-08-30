package com.baulsupp.cooee.api

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.CommandStatus
import com.squareup.moshi.Moshi
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller("command")
class CommandController(val moshi: Moshi) {
  @MessageMapping("runCommand")
  suspend fun requestResponse(request: String): String {
    val requestCommand = moshi.adapter(CommandRequest::class.java).fromJson(request)
    val response = executeRequest()
    return moshi.adapter(CommandResponse::class.java).toJson(response)
  }

  private fun executeRequest() = CommandResponse(status = CommandStatus.DONE, message = "DONE")
}