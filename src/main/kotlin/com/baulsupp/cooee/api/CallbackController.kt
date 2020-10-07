package com.baulsupp.cooee.api

import com.baulsupp.cooee.cache.AuthFlowCache
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.lang.IllegalStateException

@Controller
class CallbackController(val authFlowCache: AuthFlowCache) {
    @GetMapping("/callback")
    fun callback(
        @RequestParam(name = "error", required = false) error: String?,
        @RequestParam(name = "state", required = false) state: String?,
        model: Model,
        request: ServerHttpRequest
    ): String {
        // oauth_verifier
        val code = request.queryParams.getFirst("code") ?: request.queryParams.getFirst("oauth_verifier")

        model.addAttribute("code", code)
        model.addAttribute("error", error)

        if (state != null) {
            val result = authFlowCache.get(state)

            if (code != null) {
                result.complete(code)
            } else {
                result.completeExceptionally(IllegalStateException(error ?: "unspecified error"))
            }
        }

        return when {
          error != null -> "callbackError"
          else -> "callbackSuccess"
        }
    }
}