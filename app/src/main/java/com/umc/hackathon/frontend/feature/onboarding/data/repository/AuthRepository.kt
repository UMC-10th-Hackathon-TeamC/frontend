package com.umc.hackathon.frontend.feature.onboarding.data.repository

interface AuthRepository {
    fun getGoogleLoginUrl(): String
    suspend fun continueAsGuest()
}
