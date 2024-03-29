// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.baulsupp.cooee.p.RegisterServerResponse in api.proto
package com.baulsupp.cooee.p

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.ReverseProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import com.squareup.wire.`internal`.sanitize
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.jvm.JvmField
import okio.ByteString

public class RegisterServerResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY,
  )
  @JvmField
  public val uuid: String = "",
  unknownFields: ByteString = ByteString.EMPTY,
) : Message<RegisterServerResponse, RegisterServerResponse.Builder>(ADAPTER, unknownFields) {
  public override fun newBuilder(): Builder {
    val builder = Builder()
    builder.uuid = uuid
    builder.addUnknownFields(unknownFields)
    return builder
  }

  public override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is RegisterServerResponse) return false
    if (unknownFields != other.unknownFields) return false
    if (uuid != other.uuid) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + uuid.hashCode()
      super.hashCode = result
    }
    return result
  }

  public override fun toString(): String {
    val result = mutableListOf<String>()
    result += """uuid=${sanitize(uuid)}"""
    return result.joinToString(prefix = "RegisterServerResponse{", separator = ", ", postfix = "}")
  }

  public fun copy(uuid: String = this.uuid, unknownFields: ByteString = this.unknownFields):
      RegisterServerResponse = RegisterServerResponse(uuid, unknownFields)

  public class Builder : Message.Builder<RegisterServerResponse, Builder>() {
    @JvmField
    public var uuid: String = ""

    public fun uuid(uuid: String): Builder {
      this.uuid = uuid
      return this
    }

    public override fun build(): RegisterServerResponse = RegisterServerResponse(
      uuid = uuid,
      unknownFields = buildUnknownFields()
    )
  }

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<RegisterServerResponse> = object :
        ProtoAdapter<RegisterServerResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      RegisterServerResponse::class, 
      "type.googleapis.com/com.baulsupp.cooee.p.RegisterServerResponse", 
      PROTO_3, 
      null, 
      "api.proto"
    ) {
      public override fun encodedSize(`value`: RegisterServerResponse): Int {
        var size = value.unknownFields.size
        if (value.uuid != "") size += ProtoAdapter.STRING.encodedSizeWithTag(1, value.uuid)
        return size
      }

      public override fun encode(writer: ProtoWriter, `value`: RegisterServerResponse): Unit {
        if (value.uuid != "") ProtoAdapter.STRING.encodeWithTag(writer, 1, value.uuid)
        writer.writeBytes(value.unknownFields)
      }

      public override fun encode(writer: ReverseProtoWriter, `value`: RegisterServerResponse):
          Unit {
        writer.writeBytes(value.unknownFields)
        if (value.uuid != "") ProtoAdapter.STRING.encodeWithTag(writer, 1, value.uuid)
      }

      public override fun decode(reader: ProtoReader): RegisterServerResponse {
        var uuid: String = ""
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> uuid = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return RegisterServerResponse(
          uuid = uuid,
          unknownFields = unknownFields
        )
      }

      public override fun redact(`value`: RegisterServerResponse): RegisterServerResponse =
          value.copy(
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
