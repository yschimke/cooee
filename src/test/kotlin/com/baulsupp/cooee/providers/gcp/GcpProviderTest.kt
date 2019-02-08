package com.baulsupp.cooee.providers.gcp

import com.baulsupp.cooee.BaseProviderTest
import com.baulsupp.cooee.api.Completed
import com.baulsupp.cooee.providers.opsgenie.GcpProvider
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

class GcpProviderTest: BaseProviderTest<GcpProvider>(GcpProvider::class)
