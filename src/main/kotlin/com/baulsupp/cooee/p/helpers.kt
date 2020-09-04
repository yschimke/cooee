package com.baulsupp.cooee.p

fun CommandRequest.Companion.redirect(url: String) = CommandResponse(location = url, status = CommandStatus.DONE)

fun CommandResponse.Companion.unmatched() = CommandResponse(status = CommandStatus.REQUEST_ERROR)
