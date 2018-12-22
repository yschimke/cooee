package com.baulsupp.cooee.users

class TestUserStore : UserStore {
  override suspend fun storeUser(userEntry: UserEntry) {
  }

  override suspend fun userInfo(userToken: String) = UserEntry(userToken, userToken, "$userToken@coo.ee")
}
