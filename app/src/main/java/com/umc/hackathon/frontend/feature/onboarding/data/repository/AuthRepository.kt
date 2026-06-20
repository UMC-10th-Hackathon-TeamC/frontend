package com.umc.hackathon.frontend.feature.onboarding.data.repository

import com.umc.hackathon.frontend.core.data.AuthTokens

interface AuthRepository {
    suspend fun continueWithGoogle(): AuthTokens
    suspend fun continueAsGuest()
}
