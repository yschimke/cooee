// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.baulsupp.cooee.p.CompletionSuggestion in api.proto
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
import kotlin.jvm.JvmField
import okio.ByteString

public class CompletionSuggestion(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val word: String = "",
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val line: String = "",
  @field:WireField(
    tag = 3,
    adapter = "com.baulsupp.cooee.p.CommandSuggestion#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val command: CommandSuggestion? = null,
  @field:WireField(
    tag = 4,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  public val provider: String = "",
  unknownFields: ByteString = ByteString.EMPTY
) : Message<CompletionSuggestion, CompletionSuggestion.Builder>(ADAPTER, unknownFields) {
  public override fun newBuilder(): Builder {
    val builder = Builder()
    builder.word = word
    builder.line = line
    builder.command = command
    builder.provider = provider
    builder.addUnknownFields(unknownFields)
    return builder
  }

  public override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is CompletionSuggestion) return false
    if (unknownFields != other.unknownFields) return false
    if (word != other.word) return false
    if (line != other.line) return false
    if (command != other.command) return false
    if (provider != other.provider) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + (word?.hashCode() ?: 0)
      result = result * 37 + (line?.hashCode() ?: 0)
      result = result * 37 + (command?.hashCode() ?: 0)
      result = result * 37 + (provider?.hashCode() ?: 0)
      super.hashCode = result
    }
    return result
  }

  public override fun toString(): String {
    val result = mutableListOf<String>()
    result += """word=${sanitize(word)}"""
    result += """line=${sanitize(line)}"""
    if (command != null) result += """command=$command"""
    result += """provider=${sanitize(provider)}"""
    return result.joinToString(prefix = "CompletionSuggestion{", separator = ", ", postfix = "}")
  }

  public fun copy(
    word: String = this.word,
    line: String = this.line,
    command: CommandSuggestion? = this.command,
    provider: String = this.provider,
    unknownFields: ByteString = this.unknownFields
  ): CompletionSuggestion = CompletionSuggestion(word, line, command, provider, unknownFields)

  public class Builder : Message.Builder<CompletionSuggestion, Builder>() {
    @JvmField
    public var word: String = ""

    @JvmField
    public var line: String = ""

    @JvmField
    public var command: CommandSuggestion? = null

    @JvmField
    public var provider: String = ""

    public fun word(word: String): Builder {
      this.word = word
      return this
    }

    public fun line(line: String): Builder {
      this.line = line
      return this
    }

    public fun command(command: CommandSuggestion?): Builder {
      this.command = command
      return this
    }

    public fun provider(provider: String): Builder {
      this.provider = provider
      return this
    }

    public override fun build(): CompletionSuggestion = CompletionSuggestion(
      word = word,
      line = line,
      command = command,
      provider = provider,
      unknownFields = buildUnknownFields()
    )
  }

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<CompletionSuggestion> = object :
        ProtoAdapter<CompletionSuggestion>(
      FieldEncoding.LENGTH_DELIMITED, 
      CompletionSuggestion::class, 
      "type.googleapis.com/com.baulsupp.cooee.p.CompletionSuggestion", 
      PROTO_3, 
      null
    ) {
      public override fun encodedSize(`value`: CompletionSuggestion): Int {
        var size = value.unknownFields.size
        if (value.word != "") size += ProtoAdapter.STRING.encodedSizeWithTag(1, value.word)
        if (value.line != "") size += ProtoAdapter.STRING.encodedSizeWithTag(2, value.line)
        if (value.command != null) size += CommandSuggestion.ADAPTER.encodedSizeWithTag(3,
            value.command)
        if (value.provider != "") size += ProtoAdapter.STRING.encodedSizeWithTag(4, value.provider)
        return size
      }

      public override fun encode(writer: ProtoWriter, `value`: CompletionSuggestion): Unit {
        if (value.word != "") ProtoAdapter.STRING.encodeWithTag(writer, 1, value.word)
        if (value.line != "") ProtoAdapter.STRING.encodeWithTag(writer, 2, value.line)
        if (value.command != null) CommandSuggestion.ADAPTER.encodeWithTag(writer, 3, value.command)
        if (value.provider != "") ProtoAdapter.STRING.encodeWithTag(writer, 4, value.provider)
        writer.writeBytes(value.unknownFields)
      }

      public override fun decode(reader: ProtoReader): CompletionSuggestion {
        var word: String = ""
        var line: String = ""
        var command: CommandSuggestion? = null
        var provider: String = ""
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> word = ProtoAdapter.STRING.decode(reader)
            2 -> line = ProtoAdapter.STRING.decode(reader)
            3 -> command = CommandSuggestion.ADAPTER.decode(reader)
            4 -> provider = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return CompletionSuggestion(
          word = word,
          line = line,
          command = command,
          provider = provider,
          unknownFields = unknownFields
        )
      }

      public override fun redact(`value`: CompletionSuggestion): CompletionSuggestion = value.copy(
        command = value.command?.let(CommandSuggestion.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
