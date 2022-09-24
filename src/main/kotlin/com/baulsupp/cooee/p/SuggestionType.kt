// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.baulsupp.cooee.p.SuggestionType in api.proto
package com.baulsupp.cooee.p

import com.squareup.wire.EnumAdapter
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireEnum
import kotlin.Int
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

public enum class SuggestionType(
  public override val `value`: Int,
) : WireEnum {
  UNKNOWN(0),
  /**
   * Returns a link to redirect (message is secondary to link)
   */
  LINK(1),
  /**
   * Returns a command that can be executed via a POST, with preview etc
   */
  COMMAND(2),
  /**
   * Returns a command prefix that allows further comment
   */
  PREFIX(3),
  /**
   * Returns subcommands
   */
  LIST(4),
  /**
   * Shows a preview or information (link is secondary to message)
   */
  INFORMATION(5),
  ;

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<SuggestionType> = object : EnumAdapter<SuggestionType>(
      SuggestionType::class, 
      PROTO_3, 
      SuggestionType.UNKNOWN
    ) {
      public override fun fromValue(`value`: Int): SuggestionType? = SuggestionType.fromValue(value)
    }

    @JvmStatic
    public fun fromValue(`value`: Int): SuggestionType? = when (value) {
      0 -> UNKNOWN
      1 -> LINK
      2 -> COMMAND
      3 -> PREFIX
      4 -> LIST
      5 -> INFORMATION
      else -> null
    }
  }
}
