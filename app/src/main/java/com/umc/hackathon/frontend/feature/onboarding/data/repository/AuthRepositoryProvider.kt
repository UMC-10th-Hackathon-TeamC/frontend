package com.umc.hackathon.frontend.feature.onboarding.data.repository

import com.umc.hackathon.frontend.BuildConfig
import com.umc.hackathon.frontend.core.network.NetworkModule

object AuthRepositoryProvider {
    fun create(): AuthRepository {
        return if (BuildConfig.USE_MOCK_API) {
            FakeAuthRepository()
        } else {
            RemoteAuthRepository(NetworkModule.authApi)
        }
    }
}
