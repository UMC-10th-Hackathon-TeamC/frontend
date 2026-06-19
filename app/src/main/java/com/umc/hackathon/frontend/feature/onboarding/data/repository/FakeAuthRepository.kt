package com.umc.hackathon.frontend.feature.onboarding.data.repository

import android.net.Uri
import com.umc.hackathon.frontend.core.data.AuthTokens
import com.umc.hackathon.frontend.feature.onboarding.data.dto.parseAuthResponse
import com.umc.hackathon.frontend.feature.onboarding.data.dto.toAuthTokens

class FakeAuthRepository : AuthRepository {
    override suspend fun continueWithGoogle(): AuthTokens {
        return parseAuthResponse(FAKE_GOOGLE_RESPONSE_JSON)
            .requireData()
            .toAuthTokens()
    }

    override suspend fun continueAsGuest() = Unit

    fun createFakeGoogleCallbackUri(): Uri {
        return Uri.Builder()
            .scheme("mogi")
            .authority("oauth")
            .path("/callback")
            .appendQueryParameter("accessToken", FAKE_GOOGLE_TOKENS.accessToken)
            .appendQueryParameter("refreshToken", FAKE_GOOGLE_TOKENS.refreshToken)
            .appendQueryParameter("userId", FAKE_GOOGLE_TOKENS.userId)
            .build()
    }

    companion object {
        val FAKE_GOOGLE_TOKENS = AuthTokens(
            accessToken = "fake-access-token",
            refreshToken = "fake-refresh-token",
            userId = "1"
        )

        private val FAKE_GOOGLE_RESPONSE_JSON = """
            {
              "success": true,
              "statusCode": 200,
              "message": "login success",
              "data": {
                "accessToken": "${FAKE_GOOGLE_TOKENS.accessToken}",
                "refreshToken": "${FAKE_GOOGLE_TOKENS.refreshToken}",
                "userId": "${FAKE_GOOGLE_TOKENS.userId}"
              }
            }
        """.trimIndent()
    }
}

private fun <T> com.umc.hackathon.frontend.core.network.ApiResponse<T>.requireData(): T {
    if (!success) {
        throw IllegalStateException(message)
    }

    return data ?: throw IllegalStateException("API response data is null.")
}
