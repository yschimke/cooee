package com.baulsupp.cooee.api

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class CallbackController {
    @GetMapping("/callback")
    fun callback(
        @RequestParam(name = "code", required = false) code: String?,
        @RequestParam(name = "error", required = false) error: String?,
        @RequestParam(name = "state", required = false) state: String?,
        model: Model
    ): String {
        model.addAttribute("code", code)
        model.addAttribute("error", error)

        return when {
          error != null -> "callbackError"
          else -> "callbackSuccess"
        }
    }
}