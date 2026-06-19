package com.umc.hackathon.frontend.core.network

data class ApiResponse<T>(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val data: T?
)
