package com.umc.hackathon.frontend.feature.mypage.data.repository

import com.umc.hackathon.frontend.feature.mypage.model.UserProfile

interface MyPageRepository {
    suspend fun getMyProfile(): UserProfile?
    suspend fun logout()
}
