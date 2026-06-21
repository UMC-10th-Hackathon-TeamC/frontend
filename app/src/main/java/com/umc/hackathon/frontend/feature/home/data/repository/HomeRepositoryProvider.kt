package com.umc.hackathon.frontend.feature.home.data.repository

import com.umc.hackathon.frontend.core.network.NetworkModule

object HomeRepositoryProvider {
    fun create(): HomeRepository {
        return RemoteHomeRepository(NetworkModule.homeApi)
    }
}
