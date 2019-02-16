package com.baulsupp.cooee.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import io.netty.channel.nio.NioEventLoopGroup
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoFactory {
  fun mongo(local: Boolean, eventLoopGroup: NioEventLoopGroup): CoroutineClient = if (local) {
    localhostMongo()
  } else {
    cloudMongo(eventLoopGroup)
  }

  private fun localhostMongo(): CoroutineClient {
    val settings = MongoClientSettings.builder()
      .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
      .build()

    return KMongo.createClient(settings).coroutine
  }

  private fun cloudMongo(eventLoopGroup: NioEventLoopGroup): CoroutineClient {
    val settings = MongoClientSettings.builder()
      .streamFactoryFactory(NettyStreamFactoryFactory.builder().eventLoopGroup(eventLoopGroup).build())
      .applyConnectionString(ConnectionString("mongodb://cooee:br4PA4HyGabAkN0c@cooeedb-shard-00-00-bnhzn.gcp.mongodb.net:27017,cooeedb-shard-00-01-bnhzn.gcp.mongodb.net:27017,cooeedb-shard-00-02-bnhzn.gcp.mongodb.net:27017/cooee?ssl=true&replicaSet=CooeeDB-shard-0&authSource=admin&retryWrites=true"))
      .applyToConnectionPoolSettings {
        it.maxSize(20)
        it.maxWaitQueueSize(100)
      }
      .build()

    return KMongo.createClient(settings).coroutine
  }
}
