// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.baulsupp.cooee.p.TokenRequest in api.proto
package com.baulsupp.cooee.p

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import com.squareup.wire.`internal`.sanitize
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

public class TokenRequest(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val service: String = "",
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING_VALUE",
    label = WireField.Label.OMIT_IDENTITY,
    jsonName = "tokenSet"
  )
  @JvmField
  public val token_set: String? = null,
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#STRING_VALUE",
    label = WireField.Label.OMIT_IDENTITY,
    jsonName = "loginUrl"
  )
  @JvmField
  public val login_url: String? = null,
  @field:WireField(
    tag = 4,
    adapter = "com.squareup.wire.ProtoAdapter#STRING_VALUE",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val token: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<TokenRequest, TokenRequest.Builder>(ADAPTER, unknownFields) {
  public override fun newBuilder(): Builder {
    val builder = Builder()
    builder.service = service
    builder.token_set = token_set
    builder.login_url = login_url
    builder.token = token
    builder.addUnknownFields(unknownFields)
    return builder
  }

  public override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is TokenRequest) return false
    if (unknownFields != other.unknownFields) return false
    if (service != other.service) return false
    if (token_set != other.token_set) return false
    if (login_url != other.login_url) return false
    if (token != other.token) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + service.hashCode()
      result = result * 37 + token_set.hashCode()
      result = result * 37 + login_url.hashCode()
      result = result * 37 + token.hashCode()
      super.hashCode = result
    }
    return result
  }

  public override fun toString(): String {
    val result = mutableListOf<String>()
    result += """service=${sanitize(service)}"""
    if (token_set != null) result += """token_set=$token_set"""
    if (login_url != null) result += """login_url=$login_url"""
    if (token != null) result += """token=$token"""
    return result.joinToString(prefix = "TokenRequest{", separator = ", ", postfix = "}")
  }

  public fun copy(
    service: String = this.service,
    token_set: String? = this.token_set,
    login_url: String? = this.login_url,
    token: String? = this.token,
    unknownFields: ByteString = this.unknownFields
  ): TokenRequest = TokenRequest(service, token_set, login_url, token, unknownFields)

  public class Builder : Message.Builder<TokenRequest, Builder>() {
    @JvmField
    public var service: String = ""

    @JvmField
    public var token_set: String? = null

    @JvmField
    public var login_url: String? = null

    @JvmField
    public var token: String? = null

    public fun service(service: String): Builder {
      this.service = service
      return this
    }

    public fun token_set(token_set: String?): Builder {
      this.token_set = token_set
      return this
    }

    public fun login_url(login_url: String?): Builder {
      this.login_url = login_url
      return this
    }

    public fun token(token: String?): Builder {
      this.token = token
      return this
    }

    public override fun build(): TokenRequest = TokenRequest(
      service = service,
      token_set = token_set,
      login_url = login_url,
      token = token,
      unknownFields = buildUnknownFields()
    )
  }

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<TokenRequest> = object : ProtoAdapter<TokenRequest>(
      FieldEncoding.LENGTH_DELIMITED, 
      TokenRequest::class, 
      "type.googleapis.com/com.baulsupp.cooee.p.TokenRequest", 
      PROTO_3, 
      null
    ) {
      public override fun encodedSize(value: TokenRequest): Int {
        var size = value.unknownFields.size
        if (value.service != "") size += ProtoAdapter.STRING.encodedSizeWithTag(1, value.service)
        if (value.token_set != null) size += ProtoAdapter.STRING_VALUE.encodedSizeWithTag(2,
            value.token_set)
        if (value.login_url != null) size += ProtoAdapter.STRING_VALUE.encodedSizeWithTag(3,
            value.login_url)
        if (value.token != null) size += ProtoAdapter.STRING_VALUE.encodedSizeWithTag(4,
            value.token)
        return size
      }

      public override fun encode(writer: ProtoWriter, value: TokenRequest): Unit {
        if (value.service != "") ProtoAdapter.STRING.encodeWithTag(writer, 1, value.service)
        if (value.token_set != null) ProtoAdapter.STRING_VALUE.encodeWithTag(writer, 2,
            value.token_set)
        if (value.login_url != null) ProtoAdapter.STRING_VALUE.encodeWithTag(writer, 3,
            value.login_url)
        if (value.token != null) ProtoAdapter.STRING_VALUE.encodeWithTag(writer, 4, value.token)
        writer.writeBytes(value.unknownFields)
      }

      public override fun decode(reader: ProtoReader): TokenRequest {
        var service: String = ""
        var token_set: String? = null
        var login_url: String? = null
        var token: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> service = ProtoAdapter.STRING.decode(reader)
            2 -> token_set = ProtoAdapter.STRING_VALUE.decode(reader)
            3 -> login_url = ProtoAdapter.STRING_VALUE.decode(reader)
            4 -> token = ProtoAdapter.STRING_VALUE.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return TokenRequest(
          service = service,
          token_set = token_set,
          login_url = login_url,
          token = token,
          unknownFields = unknownFields
        )
      }

      public override fun redact(value: TokenRequest): TokenRequest = value.copy(
        token_set = value.token_set?.let(ProtoAdapter.STRING_VALUE::redact),
        login_url = value.login_url?.let(ProtoAdapter.STRING_VALUE::redact),
        token = value.token?.let(ProtoAdapter.STRING_VALUE::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
