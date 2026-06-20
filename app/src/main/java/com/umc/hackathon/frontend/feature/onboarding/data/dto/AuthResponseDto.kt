package com.umc.hackathon.frontend.feature.onboarding.data.dto

import com.umc.hackathon.frontend.core.network.ApiResponse
import org.json.JSONObject

data class AuthTokenDataDto(
    val accessToken: String,
    val refreshToken: String? = null,
    val userId: String? = null
)

fun parseAuthResponse(json: String): ApiResponse<AuthTokenDataDto> {
    val root = JSONObject(json)
    val data = root.getJSONObject("data")

    return ApiResponse(
        success = root.optBoolean("success"),
        statusCode = root.optInt("statusCode"),
        message = root.optString("message"),
        data = AuthTokenDataDto(
            accessToken = data.optString("accessToken"),
            refreshToken = data.optString("refreshToken"),
            userId = data.optString("userId")
        )
    )
}
