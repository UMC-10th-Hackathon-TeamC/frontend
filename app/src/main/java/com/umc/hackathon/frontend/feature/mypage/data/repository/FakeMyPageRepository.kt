package com.umc.hackathon.frontend.feature.mypage.data.repository

import com.umc.hackathon.frontend.core.model.MosquitoLevel
import com.umc.hackathon.frontend.feature.mypage.model.MyDistrict
import com.umc.hackathon.frontend.feature.mypage.model.UserProfile

class FakeMyPageRepository : MyPageRepository {
    private var profile = UserProfile(
        id = 1,
        nickname = "모기맵유저",
        email = "user@gmail.com",
        profileImageUrl = null,
        districtName = "강남구"
    )

    private val district = MyDistrict(
        id = 1,
        districtName = "강남구",
        mosquitoIndex = 72,
        level = MosquitoLevel.HIGH
    )

    override suspend fun getMyProfile(): UserProfile {
        return profile
    }

    override suspend fun updateNickname(nickname: String): UserProfile {
        profile = profile.copy(nickname = nickname)
        return profile
    }

    override suspend fun getCurrentDistrict(
        latitude: Double,
        longitude: Double
    ): MyDistrict {
        return district
    }

    override suspend fun logout() = Unit
}