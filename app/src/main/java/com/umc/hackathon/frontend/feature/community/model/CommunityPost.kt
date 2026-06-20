package com.umc.hackathon.frontend.feature.community.model

data class CommunityPost(
    val id: Long,
    val districtName: String,
    val category: String,
    val title: String,
    val content: String,
    val authorName: String,
    val createdAtText: String,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean = false,
    val createdAtMillis: Long? = null
)
