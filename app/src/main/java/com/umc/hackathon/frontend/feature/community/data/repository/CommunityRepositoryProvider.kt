package com.umc.hackathon.frontend.feature.community.data.repository

import com.umc.hackathon.frontend.BuildConfig
import com.umc.hackathon.frontend.core.network.NetworkModule

object CommunityRepositoryProvider {
    fun create(): CommunityRepository {
        return if (BuildConfig.USE_MOCK_API) {
            FakeCommunityRepository()
        } else {
            RemoteCommunityRepository(NetworkModule.communityApi)
        }
    }
}
