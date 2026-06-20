package com.umc.hackathon.frontend.feature.onboarding.data.api

import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.onboarding.data.dto.AuthTokenDataDto
import com.umc.hackathon.frontend.feature.onboarding.data.dto.RefreshTokenRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/refresh")
    suspend fun refreshTokens(
        @Body request: RefreshTokenRequestDto
    ): ApiResponse<AuthTokenDataDto>
}
