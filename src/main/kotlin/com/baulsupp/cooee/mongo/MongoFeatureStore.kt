package com.baulsupp.cooee.mongo

import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.runBlocking
import org.ff4j.core.Feature
import org.ff4j.exception.FeatureNotFoundException
import org.ff4j.store.AbstractFeatureStore
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

data class FeatureM(
  val _id: String,
  val enable: Boolean,
  val description: String?,
  val group: String?,
  val permissions: Set<String>
) {
  fun toFeature() = Feature(_id, enable, description, group, permissions.toMutableSet())
}

fun Feature.toFeatureM() = FeatureM(uid, isEnable, description, group, permissions)

class MongoFeatureStore(private val mongoDb: CoroutineDatabase) : AbstractFeatureStore() {
  private val featureDb: CoroutineCollection<FeatureM> by lazy {
    mongoDb.getCollection<FeatureM>(
      "features"
    )
  }

  override fun clear() {
    runBlocking {
      featureDb.deleteMany()
    }
  }

  override fun create(fp: Feature) {
    runBlocking {
      featureDb.insertOne(fp.toFeatureM())
    }
  }

  override fun exist(featId: String) = runBlocking {
    featureDb.countDocuments(eq("_id", featId)) > 0
  }

  override fun update(fp: Feature) {
    runBlocking {
      featureDb.updateOne(eq("_id", fp.uid), fp.toFeatureM())
    }
  }

  override fun read(featureUid: String): Feature? {
    println("read " + featureUid)

    return runBlocking {
      featureDb.findOne(eq("_id", featureUid))
    }?.toFeature() ?: throw FeatureNotFoundException(featureUid)
  }

  override fun delete(fpId: String) {
    runBlocking {
      featureDb.deleteMany(eq("uid", fpId))
    }
  }

  override fun readAll(): MutableMap<String, Feature> = runBlocking {
    featureDb.find().toList()
  }.map { it._id to it.toFeature() }.toMap().toMutableMap()
}
