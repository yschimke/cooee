package com.baulsupp.cooee.p

fun CommandResponse.Companion.redirect(url: String) = CommandResponse(location = url, status = CommandStatus.REDIRECT)

fun CommandResponse.Companion.unmatched() = CommandResponse(status = CommandStatus.REQUEST_ERROR)

fun CommandResponse.Companion.done(message: String) = CommandResponse(message = message, status = CommandStatus.DONE)

val CommandRequest.single_command: String?
  get() = parsed_command.firstOrNull()

val CommandRequest.args: List<String>
  get() = parsed_command.drop(1)

fun CompletionResponse.Companion.none() = CompletionResponse()

fun CompletionSuggestion.Companion.command(response: CommandSuggestion, vararg line: String): CompletionSuggestion {
  val line1 = line.joinToString(" ")
  return CompletionSuggestion(word = line.last(), line = line1,
      command = response)
}

fun LogRequest.Companion.warn(message: String): LogRequest {
  return LogRequest(message = message, severity = LogSeverity.WARN)
}
