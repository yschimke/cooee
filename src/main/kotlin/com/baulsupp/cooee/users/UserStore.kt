package com.baulsupp.cooee.users

interface UserStore {
  suspend fun userInfo(user: String): UserEntry?

  suspend fun storeUser(userEntry: UserEntry)
}
