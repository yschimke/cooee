// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: api.proto
package com.baulsupp.cooee.p

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.WireField
import com.squareup.wire.internal.sanitize
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class TokenRequest(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  @JvmField
  val service: String? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  @JvmField
  val name: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<TokenRequest, TokenRequest.Builder>(ADAPTER, unknownFields) {
  override fun newBuilder(): Builder {
    val builder = Builder()
    builder.service = service
    builder.name = name
    builder.addUnknownFields(unknownFields)
    return builder
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is TokenRequest) return false
    return unknownFields == other.unknownFields
        && service == other.service
        && name == other.name
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + service.hashCode()
      result = result * 37 + name.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (service != null) result += """service=${sanitize(service)}"""
    if (name != null) result += """name=${sanitize(name)}"""
    return result.joinToString(prefix = "TokenRequest{", separator = ", ", postfix = "}")
  }

  fun copy(
    service: String? = this.service,
    name: String? = this.name,
    unknownFields: ByteString = this.unknownFields
  ): TokenRequest = TokenRequest(service, name, unknownFields)

  class Builder : Message.Builder<TokenRequest, Builder>() {
    @JvmField
    var service: String? = null

    @JvmField
    var name: String? = null

    fun service(service: String?): Builder {
      this.service = service
      return this
    }

    fun name(name: String?): Builder {
      this.name = name
      return this
    }

    override fun build(): TokenRequest = TokenRequest(
      service = service,
      name = name,
      unknownFields = buildUnknownFields()
    )
  }

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<TokenRequest> = object : ProtoAdapter<TokenRequest>(
      FieldEncoding.LENGTH_DELIMITED, 
      TokenRequest::class, 
      "type.googleapis.com/com.baulsupp.cooee.p.TokenRequest"
    ) {
      override fun encodedSize(value: TokenRequest): Int = 
        ProtoAdapter.STRING.encodedSizeWithTag(1, value.service) +
        ProtoAdapter.STRING.encodedSizeWithTag(2, value.name) +
        value.unknownFields.size

      override fun encode(writer: ProtoWriter, value: TokenRequest) {
        ProtoAdapter.STRING.encodeWithTag(writer, 1, value.service)
        ProtoAdapter.STRING.encodeWithTag(writer, 2, value.name)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): TokenRequest {
        var service: String? = null
        var name: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> service = ProtoAdapter.STRING.decode(reader)
            2 -> name = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return TokenRequest(
          service = service,
          name = name,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: TokenRequest): TokenRequest = value.copy(
        unknownFields = ByteString.EMPTY
      )
    }
  }
}