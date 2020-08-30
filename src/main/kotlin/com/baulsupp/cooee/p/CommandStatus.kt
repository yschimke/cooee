// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: api.proto
package com.baulsupp.cooee.p

import com.squareup.wire.EnumAdapter
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.WireEnum
import kotlin.Int
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

enum class CommandStatus(
  override val value: Int
) : WireEnum {
  CLIENT_ACTION(1),

  DONE(2),

  REDIRECT(3),

  REQUEST_ERROR(4),

  SERVER_ERROR(5);

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<CommandStatus> = object : EnumAdapter<CommandStatus>(
      CommandStatus::class
    ) {
      override fun fromValue(value: Int): CommandStatus? = CommandStatus.fromValue(value)
    }

    @JvmStatic
    fun fromValue(value: Int): CommandStatus? = when (value) {
      1 -> CLIENT_ACTION
      2 -> DONE
      3 -> REDIRECT
      4 -> REQUEST_ERROR
      5 -> SERVER_ERROR
      else -> null
    }
  }
}
