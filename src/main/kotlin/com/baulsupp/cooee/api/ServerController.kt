package com.baulsupp.cooee.api

import com.baulsupp.cooee.api.RSocketClientApi.Companion.asClientApi
import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CompletionRequest
import com.baulsupp.cooee.p.LogRequest
import com.baulsupp.cooee.p.LogSeverity
import com.baulsupp.cooee.p.RegisterServerRequest
import com.baulsupp.cooee.p.RegisterServerResponse
import com.baulsupp.cooee.services.remote.RemoteProvider
import com.baulsupp.cooee.services.remote.RemoteProviderInstance
import kotlinx.coroutines.flow.collect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller

@Controller
class ServerController(val remoteProvider: RemoteProvider) {
  val logger: Logger = LoggerFactory.getLogger(ServerController::class.java)

  @MessageMapping("registerServer")
  suspend fun handle(request: RegisterServerRequest, requester: RSocketRequester, @AuthenticationPrincipal jwt: String): RegisterServerResponse {
    // requester.asClientApi()
    //   .logToClient(LogRequest("Server Connected: ${request.commands}", severity = LogSeverity.INFO))
    //
    // requester.asClientApi().commandRequest(CommandRequest(parsed_command = listOf("welcome"))).collect {
    //   println(it)
    // }
    //
    // println(requester.asClientApi().complete(CompletionRequest(line = "welcom")))

    val remoteProviderInstance =
      RemoteProviderInstance(request.commands.first(), requester.asClientApi())
    println("Adding")
    remoteProvider.servers += remoteProviderInstance

    requester.rsocket()!!.onClose().doOnTerminate {
      println("Removing")
      remoteProvider.servers.remove(remoteProviderInstance)
    }.subscribe()

    return RegisterServerResponse(uuid = request.commands.joinToString(":"))
  }

  @MessageExceptionHandler
  fun handleException(ex: Exception) {
    println("Failed $ex")
  }
}