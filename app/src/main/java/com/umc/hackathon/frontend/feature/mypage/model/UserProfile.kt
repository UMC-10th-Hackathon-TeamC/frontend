package com.umc.hackathon.frontend.feature.mypage.model

data class UserProfile(
    val id: Int,
    val nickname: String,
    val email: String,
    val profileImageUrl: String? = null,
    val districtName: String? = null
)
