package com.umc.hackathon.frontend.feature.onboarding.data.repository

import com.umc.hackathon.frontend.BuildConfig

object AuthRepositoryProvider {
    fun create(): AuthRepository {
        return if (BuildConfig.USE_MOCK_API) {
            FakeAuthRepository()
        } else {
            RemoteAuthRepository()
        }
    }
}
