package com.umc.hackathon.frontend.feature.home.data.repository

import com.umc.hackathon.frontend.BuildConfig
import com.umc.hackathon.frontend.core.network.NetworkModule

object HomeRepositoryProvider {
    fun create(): HomeRepository {
        return if (BuildConfig.USE_MOCK_API) {
            FakeHomeRepository()
        } else {
            RemoteHomeRepository(NetworkModule.homeApi)
        }
    }
}
