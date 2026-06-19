package com.umc.hackathon.frontend.feature.mypage.data.repository

import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.mypage.data.api.MyPageApi
import com.umc.hackathon.frontend.feature.mypage.data.dto.UpdateNicknameRequestDto
import com.umc.hackathon.frontend.feature.mypage.model.MyDistrict
import com.umc.hackathon.frontend.feature.mypage.model.UserProfile

class RemoteMyPageRepository(
    private val myPageApi: MyPageApi
) : MyPageRepository {
    override suspend fun getMyProfile(): UserProfile? {
        return myPageApi.getMyProfile()
            .requireData()
            .toDomain()
    }

    override suspend fun updateNickname(nickname: String): UserProfile? {
        val response = myPageApi.updateNickname(
            UpdateNicknameRequestDto(nickname = nickname)
        ).requireData()

        return getMyProfile()?.copy(
            id = response.id,
            nickname = response.nickname
        )
    }

    override suspend fun getCurrentDistrict(
        latitude: Double,
        longitude: Double
    ): MyDistrict? {
        return myPageApi.getCurrentDistrict(
            latitude = latitude,
            longitude = longitude
        ).requireData().toDomain()
    }

    override suspend fun logout() {
        myPageApi.logout().requireSuccess()
    }
}

private fun <T> ApiResponse<T>.requireData(): T {
    if (!success) {
        throw IllegalStateException(message)
    }

    return data ?: throw IllegalStateException("API response data is null.")
}

private fun <T> ApiResponse<T>.requireSuccess() {
    if (!success) {
        throw IllegalStateException(message)
    }
}