package com.baulsupp.cooee.mongo

import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.runBlocking
import org.ff4j.property.Property
import org.ff4j.property.store.AbstractPropertyStore
import org.ff4j.property.util.PropertyJsonBean
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

data class PropertyM(
  val name: String,
  val description: String?,
  val type: String,
  val value: String
) {
  fun toProperty() = PropertyJsonBean().apply {
    this.name = this@PropertyM.name
    this.description = this@PropertyM.description
    this.type = this@PropertyM.type
    this.value = this@PropertyM.value
  }.asProperty()!!
}

fun Property<*>.toPropertyM() = PropertyM(name, description, type, asString())

class MongoFeaturePropertyStore(private val mongoDb: CoroutineDatabase) : AbstractPropertyStore() {
  private val propertyDb: CoroutineCollection<PropertyM> by lazy {
    mongoDb.getCollection<PropertyM>(
      "featureProperties"
    )
  }

  override fun deleteProperty(name: String) {
    runBlocking {
      propertyDb.deleteMany(eq("name", name))
    }
  }

  override fun readAllProperties(): MutableMap<String, Property<*>> = runBlocking {
    propertyDb.find().toList()
  }.map { it.name to it.toProperty() }.toMap().toMutableMap()

  override fun readProperty(name: String): Property<*>? = runBlocking {
    propertyDb.findOne(eq("name", name))
  }?.toProperty()

  override fun listPropertyNames(): MutableSet<String> = runBlocking {
    propertyDb.find().toList()
  }.map { it.name }.toMutableSet()

  override fun <T : Any?> createProperty(value: Property<T>) {
    runBlocking {
      propertyDb.insertOne(value.toPropertyM())
    }
  }

  override fun existProperty(name: String): Boolean = runBlocking {
    propertyDb.countDocuments(eq("name", name)) > 0
  }

  override fun clear() {
    runBlocking {
      propertyDb.deleteMany()
    }
  }
}
