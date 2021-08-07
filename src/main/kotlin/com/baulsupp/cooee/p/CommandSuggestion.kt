// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.baulsupp.cooee.p.CommandSuggestion in api.proto
package com.baulsupp.cooee.p

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import com.squareup.wire.`internal`.checkElementsNotNull
import com.squareup.wire.`internal`.immutableCopyOf
import com.squareup.wire.`internal`.redactElements
import com.squareup.wire.`internal`.sanitize
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmField
import okio.ByteString

public class CommandSuggestion(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val command: String = "",
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val provider: String = "",
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val description: String = "",
  @field:WireField(
    tag = 4,
    adapter = "com.baulsupp.cooee.p.SuggestionType#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val type: SuggestionType = SuggestionType.UNKNOWN,
  children: List<CommandSuggestion> = emptyList(),
  @field:WireField(
    tag = 6,
    adapter = "com.squareup.wire.ProtoAdapter#STRING_VALUE",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val url: String? = null,
  @field:WireField(
    tag = 7,
    adapter = "com.squareup.wire.ProtoAdapter#STRING_VALUE",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val message: String? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<CommandSuggestion, CommandSuggestion.Builder>(ADAPTER, unknownFields) {
  @field:WireField(
    tag = 5,
    adapter = "com.baulsupp.cooee.p.CommandSuggestion#ADAPTER",
    label = WireField.Label.REPEATED
  )
  @JvmField
  public val children: List<CommandSuggestion> = immutableCopyOf("children", children)

  public override fun newBuilder(): Builder {
    val builder = Builder()
    builder.command = command
    builder.provider = provider
    builder.description = description
    builder.type = type
    builder.children = children
    builder.url = url
    builder.message = message
    builder.addUnknownFields(unknownFields)
    return builder
  }

  public override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is CommandSuggestion) return false
    if (unknownFields != other.unknownFields) return false
    if (command != other.command) return false
    if (provider != other.provider) return false
    if (description != other.description) return false
    if (type != other.type) return false
    if (children != other.children) return false
    if (url != other.url) return false
    if (message != other.message) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + (command?.hashCode() ?: 0)
      result = result * 37 + (provider?.hashCode() ?: 0)
      result = result * 37 + (description?.hashCode() ?: 0)
      result = result * 37 + (type?.hashCode() ?: 0)
      result = result * 37 + children.hashCode()
      result = result * 37 + (url?.hashCode() ?: 0)
      result = result * 37 + (message?.hashCode() ?: 0)
      super.hashCode = result
    }
    return result
  }

  public override fun toString(): String {
    val result = mutableListOf<String>()
    result += """command=${sanitize(command)}"""
    result += """provider=${sanitize(provider)}"""
    result += """description=${sanitize(description)}"""
    result += """type=$type"""
    if (children.isNotEmpty()) result += """children=$children"""
    if (url != null) result += """url=$url"""
    if (message != null) result += """message=$message"""
    return result.joinToString(prefix = "CommandSuggestion{", separator = ", ", postfix = "}")
  }

  public fun copy(
    command: String = this.command,
    provider: String = this.provider,
    description: String = this.description,
    type: SuggestionType = this.type,
    children: List<CommandSuggestion> = this.children,
    url: String? = this.url,
    message: String? = this.message,
    unknownFields: ByteString = this.unknownFields
  ): CommandSuggestion = CommandSuggestion(command, provider, description, type, children, url,
      message, unknownFields)

  public class Builder : Message.Builder<CommandSuggestion, Builder>() {
    @JvmField
    public var command: String = ""

    @JvmField
    public var provider: String = ""

    @JvmField
    public var description: String = ""

    @JvmField
    public var type: SuggestionType = SuggestionType.UNKNOWN

    @JvmField
    public var children: List<CommandSuggestion> = emptyList()

    @JvmField
    public var url: String? = null

    @JvmField
    public var message: String? = null

    public fun command(command: String): Builder {
      this.command = command
      return this
    }

    public fun provider(provider: String): Builder {
      this.provider = provider
      return this
    }

    public fun description(description: String): Builder {
      this.description = description
      return this
    }

    public fun type(type: SuggestionType): Builder {
      this.type = type
      return this
    }

    public fun children(children: List<CommandSuggestion>): Builder {
      checkElementsNotNull(children)
      this.children = children
      return this
    }

    public fun url(url: String?): Builder {
      this.url = url
      return this
    }

    public fun message(message: String?): Builder {
      this.message = message
      return this
    }

    public override fun build(): CommandSuggestion = CommandSuggestion(
      command = command,
      provider = provider,
      description = description,
      type = type,
      children = children,
      url = url,
      message = message,
      unknownFields = buildUnknownFields()
    )
  }

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<CommandSuggestion> = object : ProtoAdapter<CommandSuggestion>(
      FieldEncoding.LENGTH_DELIMITED, 
      CommandSuggestion::class, 
      "type.googleapis.com/com.baulsupp.cooee.p.CommandSuggestion", 
      PROTO_3, 
      null
    ) {
      public override fun encodedSize(`value`: CommandSuggestion): Int {
        var size = value.unknownFields.size
        if (value.command != "") size += ProtoAdapter.STRING.encodedSizeWithTag(1, value.command)
        if (value.provider != "") size += ProtoAdapter.STRING.encodedSizeWithTag(2, value.provider)
        if (value.description != "") size += ProtoAdapter.STRING.encodedSizeWithTag(3,
            value.description)
        if (value.type != SuggestionType.UNKNOWN) size +=
            SuggestionType.ADAPTER.encodedSizeWithTag(4, value.type)
        size += CommandSuggestion.ADAPTER.asRepeated().encodedSizeWithTag(5, value.children)
        if (value.url != null) size += ProtoAdapter.STRING_VALUE.encodedSizeWithTag(6, value.url)
        if (value.message != null) size += ProtoAdapter.STRING_VALUE.encodedSizeWithTag(7,
            value.message)
        return size
      }

      public override fun encode(writer: ProtoWriter, `value`: CommandSuggestion): Unit {
        if (value.command != "") ProtoAdapter.STRING.encodeWithTag(writer, 1, value.command)
        if (value.provider != "") ProtoAdapter.STRING.encodeWithTag(writer, 2, value.provider)
        if (value.description != "") ProtoAdapter.STRING.encodeWithTag(writer, 3, value.description)
        if (value.type != SuggestionType.UNKNOWN) SuggestionType.ADAPTER.encodeWithTag(writer, 4,
            value.type)
        CommandSuggestion.ADAPTER.asRepeated().encodeWithTag(writer, 5, value.children)
        if (value.url != null) ProtoAdapter.STRING_VALUE.encodeWithTag(writer, 6, value.url)
        if (value.message != null) ProtoAdapter.STRING_VALUE.encodeWithTag(writer, 7, value.message)
        writer.writeBytes(value.unknownFields)
      }

      public override fun decode(reader: ProtoReader): CommandSuggestion {
        var command: String = ""
        var provider: String = ""
        var description: String = ""
        var type: SuggestionType = SuggestionType.UNKNOWN
        val children = mutableListOf<CommandSuggestion>()
        var url: String? = null
        var message: String? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> command = ProtoAdapter.STRING.decode(reader)
            2 -> provider = ProtoAdapter.STRING.decode(reader)
            3 -> description = ProtoAdapter.STRING.decode(reader)
            4 -> try {
              type = SuggestionType.ADAPTER.decode(reader)
            } catch (e: ProtoAdapter.EnumConstantNotFoundException) {
              reader.addUnknownField(tag, FieldEncoding.VARINT, e.value.toLong())
            }
            5 -> children.add(CommandSuggestion.ADAPTER.decode(reader))
            6 -> url = ProtoAdapter.STRING_VALUE.decode(reader)
            7 -> message = ProtoAdapter.STRING_VALUE.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return CommandSuggestion(
          command = command,
          provider = provider,
          description = description,
          type = type,
          children = children,
          url = url,
          message = message,
          unknownFields = unknownFields
        )
      }

      public override fun redact(`value`: CommandSuggestion): CommandSuggestion = value.copy(
        children = value.children.redactElements(CommandSuggestion.ADAPTER),
        url = value.url?.let(ProtoAdapter.STRING_VALUE::redact),
        message = value.message?.let(ProtoAdapter.STRING_VALUE::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
