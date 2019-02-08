package com.baulsupp.cooee.providers.mongodb

import com.baulsupp.cooee.BaseProviderTest
import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.providers.strava.StravaProvider
import com.baulsupp.cooee.test.TestAppServices
import com.baulsupp.cooee.test.setLocalCredentials
import com.baulsupp.okurl.services.strava.StravaAuthInterceptor
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.startsWith
import org.junit.Assert.assertThat
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertTrue

class MongoDbProviderTest: BaseProviderTest<MongoDbProvider>(MongoDbProvider::class)
