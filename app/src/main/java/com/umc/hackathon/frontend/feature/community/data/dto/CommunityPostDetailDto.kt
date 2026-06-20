package com.umc.hackathon.frontend.feature.community.data.dto

import com.umc.hackathon.frontend.feature.community.model.CommunityPost

data class CommunityPostDetailDto(
    val id: Long,
    val title: String,
    val content: String,
    val category: String,
    val author: String,
    val districtName: String,
    val viewCount: Int,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val liked: Boolean? = null,
    val isLiked: Boolean? = null,
    val likedByMe: Boolean? = null
) {
    fun toDomain(): CommunityPost {
        return CommunityPost(
            id = id,
            districtName = districtName,
            category = category,
            title = title,
            content = content,
            authorName = author,
            createdAtText = updatedAt.ifBlank { createdAt }.toRelativeTimeTextFromIso(),
            likeCount = likeCount,
            commentCount = commentCount,
            isLiked = isLiked ?: liked ?: likedByMe ?: false
        )
    }
}
