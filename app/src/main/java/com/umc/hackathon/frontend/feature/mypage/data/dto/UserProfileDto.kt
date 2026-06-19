package com.umc.hackathon.frontend.feature.mypage.data.dto

import com.umc.hackathon.frontend.core.model.toMosquitoLevel
import com.umc.hackathon.frontend.feature.mypage.model.MyDistrict
import com.umc.hackathon.frontend.feature.mypage.model.UserProfile

data class UserProfileDto(
    val id: Int,
    val email: String,
    val nickname: String,
    val profileImage: String?,
    val createdAt: String
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            id = id,
            nickname = nickname,
            email = email,
            profileImageUrl = profileImage
        )
    }
}

data class UpdateNicknameRequestDto(
    val nickname: String
)

data class UpdateNicknameResponseDto(
    val id: Int,
    val nickname: String
)

data class CurrentDistrictDto(
    val id: Int,
    val name: String,
    val mosquitoIndex: Int,
    val level: String
) {
    fun toDomain(): MyDistrict {
        return MyDistrict(
            id = id,
            districtName = name,
            mosquitoIndex = mosquitoIndex,
            level = level.toMosquitoLevel()
        )
    }
}