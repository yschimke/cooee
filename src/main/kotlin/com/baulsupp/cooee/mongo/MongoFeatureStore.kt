package com.baulsupp.cooee.mongo

import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.runBlocking
import org.ff4j.core.Feature
import org.ff4j.exception.FeatureNotFoundException
import org.ff4j.store.AbstractFeatureStore
import org.ff4j.utils.MappingUtil
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

data class FeatureM(
  val _id: String,
  val enable: Boolean,
  val description: String?,
  val group: String?,
  val permissions: Set<String>,
  val strategy: String?,
  val expression: Map<String, String>?,
  val properties: List<PropertyM>?
) {
  fun toFeature(): Feature {
    return Feature(_id, enable, description, group, permissions.toMutableSet()).apply {
      properties?.forEach {
        addProperty(it.toProperty())
      }
      if (strategy != null) {
        flippingStrategy = MappingUtil.instanceFlippingStrategy(_id, strategy, expression)
      }
    }
  }
}

fun Feature.toFeatureM(): FeatureM {
  var strategy: String? = null
  var expression: Map<String, String>? = null
  var properties: List<PropertyM>? = null

  if (flippingStrategy != null) {
    strategy = flippingStrategy.javaClass.name
    expression = flippingStrategy.initParams
  }

  if (customProperties != null) {
    properties = customProperties.map { it.value.toPropertyM() }
  }

  return FeatureM(uid, isEnable, description, group, permissions, strategy, expression, properties)
}

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
    return runBlocking {
      featureDb.findOne(eq("_id", featureUid))
    }?.toFeature() ?: throw FeatureNotFoundException(featureUid)
  }

  override fun delete(fpId: String) {
    runBlocking {
      featureDb.deleteMany(eq("_id", fpId))
    }
  }

  override fun readAll(): MutableMap<String, Feature> = runBlocking {
    featureDb.find().toList()
  }.map { it._id to it.toFeature() }.toMap().toMutableMap()
}
