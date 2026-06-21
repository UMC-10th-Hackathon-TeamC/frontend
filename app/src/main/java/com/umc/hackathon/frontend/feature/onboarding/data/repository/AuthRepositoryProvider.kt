package com.umc.hackathon.frontend.feature.onboarding.data.repository

object AuthRepositoryProvider {
    fun create(): AuthRepository {
        return RemoteAuthRepository()
    }
}
