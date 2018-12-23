package com.baulsupp.cooee.mongo

import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

object UserCredentialsCodec : Codec<UserCredentials> {
  override fun getEncoderClass(): Class<UserCredentials> {
    return UserCredentials::class.java
  }

  override fun encode(writer: BsonWriter, value: UserCredentials, encoderContext: EncoderContext) {
    writer.writeStartDocument()
    writer.writeString("user", value.user)
    writer.writeString("tokenSet", value.tokenSet)
    writer.writeString("token", value.token)
    writer.writeEndDocument()
  }

  override fun decode(reader: BsonReader, decoderContext: DecoderContext): UserCredentials {
    reader.readStartDocument()

    var token: String? = null
    var user: String? = null
    var tokenSet: String? = null
    var serviceName: String? = null

    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      val name = reader.readName()
      val bsonType = reader.currentBsonType

      when {
        name == "token" -> token = reader.readString()
        name == "tokenSet" -> tokenSet = reader.readString()
        name == "user" -> user = reader.readString()
        name == "serviceName" -> serviceName = reader.readString()
        bsonType == BsonType.OBJECT_ID -> reader.readObjectId()
      }
    }

    reader.readEndDocument()

    return UserCredentials(user!!, tokenSet!!, token!!, serviceName!!)
  }
}
