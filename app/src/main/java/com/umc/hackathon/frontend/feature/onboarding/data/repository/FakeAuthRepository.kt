package com.umc.hackathon.frontend.feature.onboarding.data.repository

import android.net.Uri
import com.umc.hackathon.frontend.core.data.AuthTokens

class FakeAuthRepository : AuthRepository {
    override fun getGoogleLoginUrl(): String {
        return Uri.Builder()
            .scheme("mogi")
            .authority("oauth")
            .path("/callback")
            .appendQueryParameter("accessToken", FAKE_GOOGLE_TOKENS.accessToken)
            .appendQueryParameter("refreshToken", FAKE_GOOGLE_TOKENS.refreshToken)
            .appendQueryParameter("userId", FAKE_GOOGLE_TOKENS.userId)
            .build()
            .toString()
    }

    override suspend fun continueAsGuest() = Unit

    companion object {
        private val FAKE_GOOGLE_TOKENS = AuthTokens(
            accessToken = "fake-access-token",
            refreshToken = "fake-refresh-token",
            userId = "1"
        )
    }
}
