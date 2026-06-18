package com.umc.hackathon.frontend.feature.mypage.data.repository

import com.umc.hackathon.frontend.feature.mypage.model.UserProfile

class FakeMyPageRepository : MyPageRepository {
    override suspend fun getMyProfile(): UserProfile {
        return UserProfile(
            nickname = "모기맵유저",
            email = "user@gmail.com",
            districtName = "강남구"
        )
    }

    override suspend fun logout() = Unit
}
