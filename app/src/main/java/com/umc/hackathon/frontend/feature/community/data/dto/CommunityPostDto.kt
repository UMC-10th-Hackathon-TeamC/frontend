package com.umc.hackathon.frontend.feature.community.data.dto

data class CommunityPostDto(
    val id: Long,
    val districtName: String,
    val category: String,
    val title: String,
    val content: String,
    val authorName: String,
    val createdAt: String,
    val likeCount: Int,
    val commentCount: Int
)
