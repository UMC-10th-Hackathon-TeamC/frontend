package com.umc.hackathon.frontend.feature.onboarding.data.api

import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.onboarding.data.dto.AuthTokenDataDto
import com.umc.hackathon.frontend.feature.onboarding.data.dto.RefreshTokenRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @GET("oauth2/callback/google")
    suspend fun fetchGoogleTokens(): ApiResponse<AuthTokenDataDto>

    @POST("auth/refresh")
    suspend fun refreshTokens(
        @Body request: RefreshTokenRequestDto
    ): ApiResponse<AuthTokenDataDto>
}
