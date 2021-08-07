// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.baulsupp.cooee.p.CommandResponse in api.proto
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

public class CommandResponse(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING_VALUE",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val url: String? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING_VALUE",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val message: String? = null,
  @field:WireField(
    tag = 3,
    adapter = "com.baulsupp.cooee.p.ImageUrl#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY,
    jsonName = "imageUrl"
  )
  @JvmField
  public val image_url: ImageUrl? = null,
  @field:WireField(
    tag = 4,
    adapter = "com.baulsupp.cooee.p.CommandStatus#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val status: CommandStatus = CommandStatus.UNDEFINED,
  @field:WireField(
    tag = 5,
    adapter = "com.baulsupp.cooee.p.Table#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val table: Table? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<CommandResponse, CommandResponse.Builder>(ADAPTER, unknownFields) {
  public override fun newBuilder(): Builder {
    val builder = Builder()
    builder.url = url
    builder.message = message
    builder.image_url = image_url
    builder.status = status
    builder.table = table
    builder.addUnknownFields(unknownFields)
    return builder
  }

  public override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is CommandResponse) return false
    if (unknownFields != other.unknownFields) return false
    if (url != other.url) return false
    if (message != other.message) return false
    if (image_url != other.image_url) return false
    if (status != other.status) return false
    if (table != other.table) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + (url?.hashCode() ?: 0)
      result = result * 37 + (message?.hashCode() ?: 0)
      result = result * 37 + (image_url?.hashCode() ?: 0)
      result = result * 37 + (status?.hashCode() ?: 0)
      result = result * 37 + (table?.hashCode() ?: 0)
      super.hashCode = result
    }
    return result
  }

  public override fun toString(): String {
    val result = mutableListOf<String>()
    if (url != null) result += """url=$url"""
    if (message != null) result += """message=$message"""
    if (image_url != null) result += """image_url=$image_url"""
    result += """status=$status"""
    if (table != null) result += """table=$table"""
    return result.joinToString(prefix = "CommandResponse{", separator = ", ", postfix = "}")
  }

  public fun copy(
    url: String? = this.url,
    message: String? = this.message,
    image_url: ImageUrl? = this.image_url,
    status: CommandStatus = this.status,
    table: Table? = this.table,
    unknownFields: ByteString = this.unknownFields
  ): CommandResponse = CommandResponse(url, message, image_url, status, table, unknownFields)

  public class Builder : Message.Builder<CommandResponse, Builder>() {
    @JvmField
    public var url: String? = null

    @JvmField
    public var message: String? = null

    @JvmField
    public var image_url: ImageUrl? = null

    @JvmField
    public var status: CommandStatus = CommandStatus.UNDEFINED

    @JvmField
    public var table: Table? = null

    public fun url(url: String?): Builder {
      this.url = url
      return this
    }

    public fun message(message: String?): Builder {
      this.message = message
      return this
    }

    public fun image_url(image_url: ImageUrl?): Builder {
      this.image_url = image_url
      return this
    }

    public fun status(status: CommandStatus): Builder {
      this.status = status
      return this
    }

    public fun table(table: Table?): Builder {
      this.table = table
      return this
    }

    public override fun build(): CommandResponse = CommandResponse(
      url = url,
      message = message,
      image_url = image_url,
      status = status,
      table = table,
      unknownFields = buildUnknownFields()
    )
  }

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<CommandResponse> = object : ProtoAdapter<CommandResponse>(
      FieldEncoding.LENGTH_DELIMITED, 
      CommandResponse::class, 
      "type.googleapis.com/com.baulsupp.cooee.p.CommandResponse", 
      PROTO_3, 
      null
    ) {
      public override fun encodedSize(`value`: CommandResponse): Int {
        var size = value.unknownFields.size
        if (value.url != null) size += ProtoAdapter.STRING_VALUE.encodedSizeWithTag(1, value.url)
        if (value.message != null) size += ProtoAdapter.STRING_VALUE.encodedSizeWithTag(2,
            value.message)
        if (value.image_url != null) size += ImageUrl.ADAPTER.encodedSizeWithTag(3, value.image_url)
        if (value.status != CommandStatus.UNDEFINED) size +=
            CommandStatus.ADAPTER.encodedSizeWithTag(4, value.status)
        if (value.table != null) size += Table.ADAPTER.encodedSizeWithTag(5, value.table)
        return size
      }

      public override fun encode(writer: ProtoWriter, `value`: CommandResponse): Unit {
        if (value.url != null) ProtoAdapter.STRING_VALUE.encodeWithTag(writer, 1, value.url)
        if (value.message != null) ProtoAdapter.STRING_VALUE.encodeWithTag(writer, 2, value.message)
        if (value.image_url != null) ImageUrl.ADAPTER.encodeWithTag(writer, 3, value.image_url)
        if (value.status != CommandStatus.UNDEFINED) CommandStatus.ADAPTER.encodeWithTag(writer, 4,
            value.status)
        if (value.table != null) Table.ADAPTER.encodeWithTag(writer, 5, value.table)
        writer.writeBytes(value.unknownFields)
      }

      public override fun decode(reader: ProtoReader): CommandResponse {
        var url: String? = null
        var message: String? = null
        var image_url: ImageUrl? = null
        var status: CommandStatus = CommandStatus.UNDEFINED
        var table: Table? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> url = ProtoAdapter.STRING_VALUE.decode(reader)
            2 -> message = ProtoAdapter.STRING_VALUE.decode(reader)
            3 -> image_url = ImageUrl.ADAPTER.decode(reader)
            4 -> try {
              status = CommandStatus.ADAPTER.decode(reader)
            } catch (e: ProtoAdapter.EnumConstantNotFoundException) {
              reader.addUnknownField(tag, FieldEncoding.VARINT, e.value.toLong())
            }
            5 -> table = Table.ADAPTER.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return CommandResponse(
          url = url,
          message = message,
          image_url = image_url,
          status = status,
          table = table,
          unknownFields = unknownFields
        )
      }

      public override fun redact(`value`: CommandResponse): CommandResponse = value.copy(
        url = value.url?.let(ProtoAdapter.STRING_VALUE::redact),
        message = value.message?.let(ProtoAdapter.STRING_VALUE::redact),
        image_url = value.image_url?.let(ImageUrl.ADAPTER::redact),
        table = value.table?.let(Table.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
