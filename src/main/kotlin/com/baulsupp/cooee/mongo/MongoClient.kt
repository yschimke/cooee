package com.baulsupp.cooee.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup

object MongoFactory {
  var local = true

  lateinit var mongoClient: MongoClient

  lateinit var eventLoopGroup: EventLoopGroup

  fun localhostMongo(): MongoClient {
    return MongoClients.create()
  }

  fun cloudMongo(): MongoClient {
    if (!this::eventLoopGroup.isInitialized) {
      eventLoopGroup = NioEventLoopGroup()
    }

    val settings = MongoClientSettings.builder()
      .streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
      .applyConnectionString(ConnectionString("mongodb+srv://cooee:78512WuwCeuvzrru@cooee0-bnhzn.gcp.mongodb.net/test?retryWrites=true"))
      .build()

    return MongoClients.create(settings)
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
