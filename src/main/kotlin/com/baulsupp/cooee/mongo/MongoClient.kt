package com.baulsupp.cooee.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import io.netty.channel.nio.NioEventLoopGroup
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry

object MongoFactory {
  private fun codecRegistry(): CodecRegistry {
    return CodecRegistries.fromRegistries(
      CodecRegistries.fromCodecs(ProviderInstanceCodec, UserCredentialsCodec),
      MongoClientSettings.getDefaultCodecRegistry()
    )
  }

  fun mongo(local: Boolean, eventLoopGroup: NioEventLoopGroup): MongoClient = if (local) {
    localhostMongo()
  } else {
    cloudMongo(eventLoopGroup)
  }

  fun localhostMongo(): MongoClient {
    val settings = MongoClientSettings.builder()
      .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
      .codecRegistry(codecRegistry())
      .build()

    return MongoClients.create(settings)
  }

  fun cloudMongo(eventLoopGroup: NioEventLoopGroup): MongoClient {
    val settings = MongoClientSettings.builder()
      .streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
      .applyConnectionString(ConnectionString("mongodb://cooee:br4PA4HyGabAkN0c@cooeedb-shard-00-00-bnhzn.gcp.mongodb.net:27017,cooeedb-shard-00-01-bnhzn.gcp.mongodb.net:27017,cooeedb-shard-00-02-bnhzn.gcp.mongodb.net:27017/cooee?ssl=true&replicaSet=CooeeDB-shard-0&authSource=admin&retryWrites=true"))
      .codecRegistry(codecRegistry())
      .build()

    return MongoClients.create(settings)
  }
}
