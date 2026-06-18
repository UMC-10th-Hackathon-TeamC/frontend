package com.umc.hackathon.frontend.feature.onboarding.data.repository

interface AuthRepository {
    suspend fun continueWithGoogle()
    suspend fun continueAsGuest()
}
