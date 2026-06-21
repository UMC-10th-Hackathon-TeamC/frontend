package com.umc.hackathon.frontend.feature.community.data.repository

import com.umc.hackathon.frontend.core.network.NetworkModule

object CommunityRepositoryProvider {
    fun create(): CommunityRepository {
        return RemoteCommunityRepository(NetworkModule.communityApi)
    }
}
