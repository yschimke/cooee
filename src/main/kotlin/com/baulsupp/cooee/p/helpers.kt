package com.baulsupp.cooee.p

fun CommandResponse.Companion.redirect(url: String) = CommandResponse(location = url, status = CommandStatus.REDIRECT)

fun CommandResponse.Companion.unmatched() = CommandResponse(status = CommandStatus.REQUEST_ERROR)

fun CommandResponse.Companion.done(message: String) = CommandResponse(message = message, status = CommandStatus.DONE)

val CommandRequest.single_command: String
  get() = parsed_command.first()

val CommandRequest.args: List<String>
  get() = parsed_command.drop(1)
