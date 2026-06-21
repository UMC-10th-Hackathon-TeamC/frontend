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
        /* 로그인 요청에는 아직 토큰이 없으므로 Authorization 헤더를 붙이지 않음 */
        if (request.url.isAuthRequest()) {
            return chain.proceed(request)
        }

        /* OkHttp 인터셉터는 suspend를 직접 쓸 수 없어 저장된 토큰을 동기적으로 조회 */
        val accessToken = runBlocking {
            authTokenStore.tokens.first().accessToken
        }

        if (accessToken.isBlank()) {
            return chain.proceed(request)
        }

        /* 로그인 이후 API 요청에 Bearer accessToken을 자동으로 추가 */
        val authorizedRequest = request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(authorizedRequest)
    }
}
