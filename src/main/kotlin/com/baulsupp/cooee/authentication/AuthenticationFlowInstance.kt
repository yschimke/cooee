package com.baulsupp.cooee.authentication

data class AuthenticationFlowInstance(val state: String, val token: String, val service: String)
