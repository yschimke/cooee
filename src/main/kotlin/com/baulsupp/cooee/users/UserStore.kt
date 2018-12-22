package com.baulsupp.cooee.users

interface UserStore {
  suspend fun userInfo(userToken: String): UserEntry?

  suspend fun storeUser(userEntry: UserEntry)
}
