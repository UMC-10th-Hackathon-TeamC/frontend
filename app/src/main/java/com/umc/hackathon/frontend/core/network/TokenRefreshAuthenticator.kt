package com.umc.hackathon.frontend.core.network

import com.umc.hackathon.frontend.core.data.AuthTokenStore
import com.umc.hackathon.frontend.feature.onboarding.data.api.AuthApi
import com.umc.hackathon.frontend.feature.onboarding.data.dto.RefreshTokenRequestDto
import com.umc.hackathon.frontend.feature.onboarding.data.dto.toAuthTokens
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator(
    private val authTokenStore: AuthTokenStore,
    private val authApi: AuthApi
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.isAuthRequest() || response.responseCount >= MAX_RETRY_COUNT) {
            return null
        }

        return runBlocking {
            val currentTokens = authTokenStore.tokens.first()
            if (currentTokens.refreshToken.isBlank()) {
                authTokenStore.clearTokens()
                return@runBlocking null
            }

            val refreshedTokens = runCatching {
                authApi.refreshTokens(
                    RefreshTokenRequestDto(refreshToken = currentTokens.refreshToken)
                )
            }.getOrNull()

            if (refreshedTokens?.success != true || refreshedTokens.data == null) {
                authTokenStore.clearTokens()
                return@runBlocking null
            }

            val newTokens = refreshedTokens.data.toAuthTokens()
            authTokenStore.saveTokens(
                accessToken = newTokens.accessToken,
                refreshToken = newTokens.refreshToken.ifBlank { currentTokens.refreshToken },
                userId = newTokens.userId.ifBlank { currentTokens.userId }
            )

            response.request.newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        }
    }

    private val Response.responseCount: Int
        get() {
            var count = 1
            var prior = this.priorResponse
            while (prior != null) {
                count++
                prior = prior.priorResponse
            }
            return count
        }

    private companion object {
        const val MAX_RETRY_COUNT = 2
    }
}
