package com.umc.hackathon.frontend.feature.community.data.dto

data class CreatePostRequestDto(
    val districtId: Int,
    val category: String,
    val title: String,
    val content: String
)
