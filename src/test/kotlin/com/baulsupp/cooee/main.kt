package com.baulsupp.cooee

import com.baulsupp.cooee.mongo.MongoCredentialsStore
import com.baulsupp.cooee.mongo.MongoFactory
import com.baulsupp.cooee.mongo.MongoProviderStore
import com.baulsupp.cooee.mongo.MongoUserStore
import com.baulsupp.cooee.providers.RegistryProvider
import com.baulsupp.cooee.providers.defaultProviders
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Token
import com.baulsupp.okurl.credentials.DefaultToken
import com.baulsupp.okurl.kotlin.client
import com.baulsupp.okurl.services.circleci.CircleCIAuthInterceptor
import com.baulsupp.okurl.services.twitter.TwitterServiceDefinition

suspend fun main(args: Array<String>) {
  val db = MongoFactory.mongoDb()

  val userStore = MongoUserStore()
//  println(userStore.userInfo("eyJhbGciOiJub25lIn0.eyJ1c2VyIjoieXVyaSIsInNlY3JldCI6IjE0ZWNiZDMxLTJjZmYtNGZkMS1hOTA4LWU3ZDBkM2VmN2EzZSIsImVtYWlsIjoieXVyaUBzY2hpbWtlLmVlIn0."))
//
  val providerStore = MongoProviderStore(RegistryProvider(defaultProviders(client)))
//
//  val x = providerStore.forUser("yuri")
//
//  providerStore.store(ProviderInstance("yuri", "google", mapOf("a" to mapOf("b" to 2))))
//
//  println(x?.providers)

  val credentialStore = MongoCredentialsStore("yuri")

  val serviceDefinition = CircleCIAuthInterceptor().serviceDefinition
  println(credentialStore.get(serviceDefinition, DefaultToken))

  credentialStore.set(serviceDefinition, DefaultToken.name, Oauth2Token("mytoken"))

  println(credentialStore.get(serviceDefinition, DefaultToken))

//  val v = Jwts.builder().claim("user", "yuri").compact()
//
//  println(v)
//
//  var jwt = Jwts.parser().parseClaimsJwt(v)
//
//  println(jwt.body)
}
