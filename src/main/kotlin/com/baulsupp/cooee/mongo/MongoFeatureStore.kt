package com.baulsupp.cooee.mongo

import org.ff4j.core.Feature
import org.ff4j.store.AbstractFeatureStore
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoFeatureStore(private val mongoDb: CoroutineDatabase): AbstractFeatureStore() {
  override fun clear() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun create(fp: Feature?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun exist(featId: String?): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun update(fp: Feature?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun read(featureUid: String?): Feature {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun delete(fpId: String?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun readAll(): MutableMap<String, Feature> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}
