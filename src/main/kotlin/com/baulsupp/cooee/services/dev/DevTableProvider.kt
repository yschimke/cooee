package com.baulsupp.cooee.services.dev

import com.baulsupp.cooee.p.*
import com.baulsupp.cooee.services.Provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DevTableProvider: Provider("dev:table") {
  override suspend fun matches(command: String): Boolean {
    return command == "dev:table"
  }

  override suspend fun runCommand(request: CommandRequest): Flow<CommandResponse>? {
    val table = Table(columns = listOf(TableColumn("A", listOf("a1", "a2")), TableColumn("B", listOf("b1", "b2"))))
    return flowOf(CommandResponse(message = "table", status = CommandStatus.DONE, table = table))
  }
}