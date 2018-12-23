package com.baulsupp.cooee.test

import com.baulsupp.cooee.users.UserEntry
import com.baulsupp.cooee.users.UserStore

class TestUserStore : UserStore {
  private val users = mutableSetOf<UserEntry>()

  override suspend fun storeUser(userEntry: UserEntry) {
    users += userEntry
  }

  override suspend fun userInfo(userToken: String): UserEntry? = users.find { it.token == userToken }
}
