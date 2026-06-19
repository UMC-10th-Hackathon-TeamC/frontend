package com.umc.hackathon.frontend.feature.mypage.data.repository

import com.umc.hackathon.frontend.feature.mypage.model.MyDistrict
import com.umc.hackathon.frontend.feature.mypage.model.UserProfile

interface MyPageRepository {
    suspend fun getMyProfile(): UserProfile?

    suspend fun updateNickname(nickname: String): UserProfile?

    suspend fun getCurrentDistrict(
        latitude: Double,
        longitude: Double
    ): MyDistrict?

    suspend fun logout()
}