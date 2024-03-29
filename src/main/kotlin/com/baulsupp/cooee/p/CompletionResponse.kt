// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.baulsupp.cooee.p.CompletionResponse in api.proto
package com.baulsupp.cooee.p

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.ReverseProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import com.squareup.wire.`internal`.checkElementsNotNull
import com.squareup.wire.`internal`.immutableCopyOf
import com.squareup.wire.`internal`.redactElements
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmField
import okio.ByteString

public class CompletionResponse(
  completions: List<CompletionSuggestion> = emptyList(),
  unknownFields: ByteString = ByteString.EMPTY,
) : Message<CompletionResponse, CompletionResponse.Builder>(ADAPTER, unknownFields) {
  @field:WireField(
    tag = 1,
    adapter = "com.baulsupp.cooee.p.CompletionSuggestion#ADAPTER",
    label = WireField.Label.REPEATED,
  )
  @JvmField
  public val completions: List<CompletionSuggestion> = immutableCopyOf("completions", completions)

  public override fun newBuilder(): Builder {
    val builder = Builder()
    builder.completions = completions
    builder.addUnknownFields(unknownFields)
    return builder
  }

  public override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is CompletionResponse) return false
    if (unknownFields != other.unknownFields) return false
    if (completions != other.completions) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + completions.hashCode()
      super.hashCode = result
    }
    return result
  }

  public override fun toString(): String {
    val result = mutableListOf<String>()
    if (completions.isNotEmpty()) result += """completions=$completions"""
    return result.joinToString(prefix = "CompletionResponse{", separator = ", ", postfix = "}")
  }

  public fun copy(completions: List<CompletionSuggestion> = this.completions,
      unknownFields: ByteString = this.unknownFields): CompletionResponse =
      CompletionResponse(completions, unknownFields)

  public class Builder : Message.Builder<CompletionResponse, Builder>() {
    @JvmField
    public var completions: List<CompletionSuggestion> = emptyList()

    public fun completions(completions: List<CompletionSuggestion>): Builder {
      checkElementsNotNull(completions)
      this.completions = completions
      return this
    }

    public override fun build(): CompletionResponse = CompletionResponse(
      completions = completions,
      unknownFields = buildUnknownFields()
    )
  }

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<CompletionResponse> = object :
        ProtoAdapter<CompletionResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      CompletionResponse::class, 
      "type.googleapis.com/com.baulsupp.cooee.p.CompletionResponse", 
      PROTO_3, 
      null, 
      "api.proto"
    ) {
      public override fun encodedSize(`value`: CompletionResponse): Int {
        var size = value.unknownFields.size
        size += CompletionSuggestion.ADAPTER.asRepeated().encodedSizeWithTag(1, value.completions)
        return size
      }

      public override fun encode(writer: ProtoWriter, `value`: CompletionResponse): Unit {
        CompletionSuggestion.ADAPTER.asRepeated().encodeWithTag(writer, 1, value.completions)
        writer.writeBytes(value.unknownFields)
      }

      public override fun encode(writer: ReverseProtoWriter, `value`: CompletionResponse): Unit {
        writer.writeBytes(value.unknownFields)
        CompletionSuggestion.ADAPTER.asRepeated().encodeWithTag(writer, 1, value.completions)
      }

      public override fun decode(reader: ProtoReader): CompletionResponse {
        val completions = mutableListOf<CompletionSuggestion>()
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> completions.add(CompletionSuggestion.ADAPTER.decode(reader))
            else -> reader.readUnknownField(tag)
          }
        }
        return CompletionResponse(
          completions = completions,
          unknownFields = unknownFields
        )
      }

      public override fun redact(`value`: CompletionResponse): CompletionResponse = value.copy(
        completions = value.completions.redactElements(CompletionSuggestion.ADAPTER),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
