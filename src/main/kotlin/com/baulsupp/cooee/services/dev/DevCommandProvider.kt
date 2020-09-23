package com.baulsupp.cooee.services.dev

import com.baulsupp.cooee.p.CommandRequest
import com.baulsupp.cooee.p.CommandResponse
import com.baulsupp.cooee.p.done
import com.baulsupp.cooee.services.Provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DevCommandProvider: Provider("dev:command") {
  override suspend fun matches(command: String): Boolean {
    return command == "dev:command"
  }

  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    return flowOf(CommandResponse.done("dev command done"))
  }
}