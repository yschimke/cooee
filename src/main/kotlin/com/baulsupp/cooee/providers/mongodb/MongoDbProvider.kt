package com.baulsupp.cooee.providers.mongodb

import com.baulsupp.cooee.api.GoResult
import com.baulsupp.cooee.api.RedirectResult
import com.baulsupp.cooee.api.Unmatched
import com.baulsupp.cooee.providers.BaseProvider

class MongoDbProvider : BaseProvider() {
  override val name = "mongodb"

  override suspend fun go(command: String, vararg args: String): GoResult = when {
    args.isEmpty() -> RedirectResult("https://cloud.mongodb.com")
    else -> Unmatched
  }
}
