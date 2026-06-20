package com.umc.hackathon.frontend.feature.onboarding.data.repository

import com.umc.hackathon.frontend.BuildConfig

class RemoteAuthRepository : AuthRepository {
    override fun getGoogleLoginUrl(): String {
        return "${BuildConfig.API_BASE_URL}oauth2/login/google"
    }

    override suspend fun continueAsGuest() = Unit
}
