package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.providers.ProviderInstance
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.MapCodec

object ProviderInstanceCodec : Codec<ProviderInstance> {
  override fun getEncoderClass(): Class<ProviderInstance> {
    return ProviderInstance::class.java
  }

  override fun encode(writer: BsonWriter, value: ProviderInstance, encoderContext: EncoderContext) {
    writer.writeStartDocument()
    writer.writeString("user", value.user)
    writer.writeString("name", value.name)
    encoderContext.encodeWithChildContext(MapCodec(), writer, value.config)
    writer.writeEndDocument()
  }

  override fun decode(reader: BsonReader, decoderContext: DecoderContext): ProviderInstance {
    reader.readStartDocument()

    var user: String? = null
    var name: String? = null
    var config: Map<String, Any>? = null

    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      val fieldName = reader.readName()
      val bsonType = reader.currentBsonType

      when {
        fieldName == "name" -> name = reader.readString()
        fieldName == "user" -> user = reader.readString()
        fieldName == "config" -> config = decoderContext.decodeWithChildContext(MapCodec(), reader)
        bsonType == BsonType.OBJECT_ID -> reader.readObjectId()
      }
    }

    reader.readEndDocument()

    return ProviderInstance(user!!, name!!, config!!)
  }
}
