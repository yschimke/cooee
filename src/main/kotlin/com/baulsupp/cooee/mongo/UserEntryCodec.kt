package com.baulsupp.cooee.mongo

import com.baulsupp.cooee.users.UserEntry
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

class UserEntryCodec : Codec<UserEntry> {
  override fun getEncoderClass(): Class<UserEntry> {
    return UserEntry::class.java
  }

  override fun encode(writer: BsonWriter, value: UserEntry, encoderContext: EncoderContext) {
    writer.writeStartDocument()
    writer.writeString("user", value.user)
    writer.writeString("email", value.email)
    writer.writeString("token", value.token)
    writer.writeEndDocument()
  }

  override fun decode(reader: BsonReader, decoderContext: DecoderContext): UserEntry {
    reader.readStartDocument()

    var token: String? = null
    var user: String? = null
    var email: String? = null

    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      val name = reader.readName()
      val bsonType = reader.currentBsonType

      when {
        name == "token" -> token = reader.readString()
        name == "email" -> email = reader.readString()
        name == "user" -> user = reader.readString()
        bsonType == BsonType.OBJECT_ID -> reader.readObjectId()
      }
    }

    reader.readEndDocument()

    return UserEntry(token!!, user!!, email)
  }
}
