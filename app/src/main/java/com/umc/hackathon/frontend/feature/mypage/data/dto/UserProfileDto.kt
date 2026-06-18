package com.umc.hackathon.frontend.feature.mypage.data.dto

data class UserProfileDto(
    val nickname: String,
    val email: String,
    val profileImageUrl: String?,
    val districtName: String?
)
