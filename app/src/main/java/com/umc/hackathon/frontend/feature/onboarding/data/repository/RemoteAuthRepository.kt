package com.umc.hackathon.frontend.feature.onboarding.data.repository

import com.umc.hackathon.frontend.core.data.AuthTokens
import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.onboarding.data.api.AuthApi
import com.umc.hackathon.frontend.feature.onboarding.data.dto.toAuthTokens

class RemoteAuthRepository(
    private val authApi: AuthApi
) : AuthRepository {
    override suspend fun continueWithGoogle(): AuthTokens {
        return authApi.fetchGoogleTokens()
            .requireData()
            .toAuthTokens()
    }

    override suspend fun continueAsGuest() = Unit
}

private fun <T> ApiResponse<T>.requireData(): T {
    if (!success) {
        throw IllegalStateException(message)
    }

    return data ?: throw IllegalStateException("API response data is null.")
}
