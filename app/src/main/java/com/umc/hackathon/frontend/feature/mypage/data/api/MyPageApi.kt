package com.umc.hackathon.frontend.feature.mypage.data.api

import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.mypage.data.dto.CurrentDistrictDto
import com.umc.hackathon.frontend.feature.mypage.data.dto.UpdateNicknameRequestDto
import com.umc.hackathon.frontend.feature.mypage.data.dto.UpdateNicknameResponseDto
import com.umc.hackathon.frontend.feature.mypage.data.dto.UserProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query


interface MyPageApi {
    @GET("users/me")
    suspend fun getMyProfile(): ApiResponse<UserProfileDto>

    @PATCH("users/me")
    suspend fun updateNickname(
        @Body request: UpdateNicknameRequestDto
    ): ApiResponse<UpdateNicknameResponseDto>

    @GET("users/me/district")
    suspend fun getCurrentDistrict(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): ApiResponse<CurrentDistrictDto>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>
}