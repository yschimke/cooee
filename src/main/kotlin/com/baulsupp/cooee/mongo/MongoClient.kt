package com.baulsupp.cooee.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry

object MongoFactory {
  var local = true

  lateinit var mongoClient: MongoClient

  lateinit var eventLoopGroup: EventLoopGroup

  fun localhostMongo(): MongoClient {
    val settings = MongoClientSettings.builder()
      .codecRegistry(codecRegistry())
      .build()

    return MongoClients.create(settings)
  }

  fun cloudMongo(): MongoClient {
    if (!this::eventLoopGroup.isInitialized) {
      eventLoopGroup = NioEventLoopGroup()
    }

    val settings = MongoClientSettings.builder()
      .streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
      .applyConnectionString(ConnectionString("mongodb://cooee:br4PA4HyGabAkN0c@cooeedb-shard-00-00-bnhzn.gcp.mongodb.net:27017,cooeedb-shard-00-01-bnhzn.gcp.mongodb.net:27017,cooeedb-shard-00-02-bnhzn.gcp.mongodb.net:27017/test?ssl=true&replicaSet=CooeeDB-shard-0&authSource=admin&retryWrites=true"))
      .codecRegistry(codecRegistry())
      .build()

    return MongoClients.create(settings)
  }

  private fun codecRegistry(): CodecRegistry {
    return CodecRegistries.fromRegistries(
      CodecRegistries.fromCodecs(UserEntryCodec, ProviderInstanceCodec, UserCredentialsCodec),
      MongoClientSettings.getDefaultCodecRegistry()
    )
  }

  fun mongo(): MongoClient {
    initialize()

    return mongoClient
  }

  fun mongoDb(): MongoDatabase {
    return mongo().getDatabase("cooee")
  }

  fun initialize() {
    if (this::mongoClient.isInitialized) {
      return
    }

    if (local) {
      mongoClient = localhostMongo()
    } else {
      mongoClient = cloudMongo()
    }
  }

  fun close() {
    if (this::mongoClient.isInitialized) {
      this.mongoClient.close()
    }

    if (this::eventLoopGroup.isInitialized) {
      this.eventLoopGroup.shutdownGracefully()
    }
  }
}
