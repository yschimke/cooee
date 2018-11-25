package com.baulsupp.cooee

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.channels.filterNotNull
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.runBlocking

@KtorExperimentalAPI
object WsClientApp {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            val client = HttpClient(CIO).config { install(WebSockets) }
            client.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/myws/echo") {
                send(Frame.Text("Hello World"))
                for (message in incoming.map { it as? Frame.Text }.filterNotNull()) {
                    println("Server said: " + message.readText())
                }
            }
        }
    }
}
