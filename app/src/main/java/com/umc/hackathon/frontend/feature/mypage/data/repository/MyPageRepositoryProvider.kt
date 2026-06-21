package com.umc.hackathon.frontend.feature.mypage.data.repository

import com.umc.hackathon.frontend.core.network.NetworkModule

object MyPageRepositoryProvider {
    fun create(): MyPageRepository {
        return RemoteMyPageRepository(NetworkModule.myPageApi)
    }
}
