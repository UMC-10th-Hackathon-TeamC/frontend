package com.umc.hackathon.frontend.core.network

import com.umc.hackathon.frontend.core.data.AuthTokenStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authTokenStore: AuthTokenStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url.isAuthRequest()) {
            return chain.proceed(request)
        }

        val accessToken = runBlocking {
            authTokenStore.tokens.first().accessToken
        }

        if (accessToken.isBlank()) {
            return chain.proceed(request)
        }

        val authorizedRequest = request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(authorizedRequest)
    }
}
