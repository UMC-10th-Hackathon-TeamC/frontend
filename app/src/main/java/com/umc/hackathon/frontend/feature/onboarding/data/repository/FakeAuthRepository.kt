package com.umc.hackathon.frontend.feature.onboarding.data.repository

class FakeAuthRepository : AuthRepository {
    override suspend fun continueWithGoogle() = Unit

    override suspend fun continueAsGuest() = Unit
}
