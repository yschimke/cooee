package com.baulsupp.cooee.services.github

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Mutation
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.request.RequestHeaders
import com.baulsupp.okurl.authenticator.oauth2.Oauth2Token
import com.baulsupp.okurl.credentials.Token
import com.baulsupp.okurl.credentials.TokenValue

suspend fun <D : Operation.Data, T, V : Operation.Variables> ApolloClient.graphqlMutation(
  meMutation: Mutation<D, T, V>,
  token: Token
): Response<T> {
  return mutate(meMutation)
      .apply {
        setAuthToken<T>(token)
      }
      .await()
}

private fun <T> ApolloQueryCall<T>.setAuthToken(t: Token): ApolloQueryCall<T> {
  val tokenString = ((t as? TokenValue)?.token as? Oauth2Token)?.accessToken
  return if (tokenString != null) {
    toBuilder()
        .requestHeaders(RequestHeaders.Builder()
            .addHeader("Authorization", "token $tokenString")
            .build())
        .build()
  } else {
    this
  }
}

private fun <T> ApolloMutationCall<T>.setAuthToken(t: Token): ApolloMutationCall<T> {
  val tokenString = ((t as? TokenValue)?.token as? Oauth2Token)?.accessToken
  return if (tokenString != null) {
    toBuilder()
        .requestHeaders(RequestHeaders.Builder()
            .addHeader("Authorization", "token $tokenString")
            .build())
        .build()
  } else {
    this
  }
}

suspend fun <D : Operation.Data, T, V : Operation.Variables> ApolloClient.graphqlQuery(
  meQuery: Query<D, T, V>,
  token: Token
): Response<T> {
  return query(meQuery)
      .run {
        setAuthToken<T>(token)
      }
      .await()
}
