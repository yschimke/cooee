// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.baulsupp.cooee.p.TokenResponse in api.proto
package com.baulsupp.cooee.p

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.jvm.JvmField
import okio.ByteString

public class TokenResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.baulsupp.cooee.p.TokenUpdate#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val token: TokenUpdate? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#BOOL",
    label = WireField.Label.OMIT_IDENTITY,
    jsonName = "loginAttempted"
  )
  @JvmField
  public val login_attempted: Boolean = false,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<TokenResponse, TokenResponse.Builder>(ADAPTER, unknownFields) {
  public override fun newBuilder(): Builder {
    val builder = Builder()
    builder.token = token
    builder.login_attempted = login_attempted
    builder.addUnknownFields(unknownFields)
    return builder
  }

  public override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is TokenResponse) return false
    if (unknownFields != other.unknownFields) return false
    if (token != other.token) return false
    if (login_attempted != other.login_attempted) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + (token?.hashCode() ?: 0)
      result = result * 37 + (login_attempted?.hashCode() ?: 0)
      super.hashCode = result
    }
    return result
  }

  public override fun toString(): String {
    val result = mutableListOf<String>()
    if (token != null) result += """token=$token"""
    result += """login_attempted=$login_attempted"""
    return result.joinToString(prefix = "TokenResponse{", separator = ", ", postfix = "}")
  }

  public fun copy(
    token: TokenUpdate? = this.token,
    login_attempted: Boolean = this.login_attempted,
    unknownFields: ByteString = this.unknownFields
  ): TokenResponse = TokenResponse(token, login_attempted, unknownFields)

  public class Builder : Message.Builder<TokenResponse, Builder>() {
    @JvmField
    public var token: TokenUpdate? = null

    @JvmField
    public var login_attempted: Boolean = false

    public fun token(token: TokenUpdate?): Builder {
      this.token = token
      return this
    }

    public fun login_attempted(login_attempted: Boolean): Builder {
      this.login_attempted = login_attempted
      return this
    }

    public override fun build(): TokenResponse = TokenResponse(
      token = token,
      login_attempted = login_attempted,
      unknownFields = buildUnknownFields()
    )
  }

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<TokenResponse> = object : ProtoAdapter<TokenResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      TokenResponse::class, 
      "type.googleapis.com/com.baulsupp.cooee.p.TokenResponse", 
      PROTO_3, 
      null
    ) {
      public override fun encodedSize(`value`: TokenResponse): Int {
        var size = value.unknownFields.size
        if (value.token != null) size += TokenUpdate.ADAPTER.encodedSizeWithTag(1, value.token)
        if (value.login_attempted != false) size += ProtoAdapter.BOOL.encodedSizeWithTag(2,
            value.login_attempted)
        return size
      }

      public override fun encode(writer: ProtoWriter, `value`: TokenResponse): Unit {
        if (value.token != null) TokenUpdate.ADAPTER.encodeWithTag(writer, 1, value.token)
        if (value.login_attempted != false) ProtoAdapter.BOOL.encodeWithTag(writer, 2,
            value.login_attempted)
        writer.writeBytes(value.unknownFields)
      }

      public override fun decode(reader: ProtoReader): TokenResponse {
        var token: TokenUpdate? = null
        var login_attempted: Boolean = false
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> token = TokenUpdate.ADAPTER.decode(reader)
            2 -> login_attempted = ProtoAdapter.BOOL.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return TokenResponse(
          token = token,
          login_attempted = login_attempted,
          unknownFields = unknownFields
        )
      }

      public override fun redact(`value`: TokenResponse): TokenResponse = value.copy(
        token = value.token?.let(TokenUpdate.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
