package com.umc.hackathon.frontend.feature.mypage.data.repository

import com.umc.hackathon.frontend.BuildConfig
import com.umc.hackathon.frontend.core.network.NetworkModule

object MyPageRepositoryProvider {
    fun create(): MyPageRepository {
        return if (BuildConfig.USE_MOCK_API) {
            FakeMyPageRepository()
        } else {
            RemoteMyPageRepository(NetworkModule.myPageApi)
        }
    }
}