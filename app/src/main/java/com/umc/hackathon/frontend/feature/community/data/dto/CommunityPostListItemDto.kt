package com.umc.hackathon.frontend.feature.community.data.dto

import com.umc.hackathon.frontend.feature.community.model.CommunityPost

data class CommunityPostListItemDto(
    val id: Long,
    val title: String,
    val category: String,
    val author: String,
    val viewCount: Int,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String,
    val liked: Boolean? = null,
    val isLiked: Boolean? = null,
    val likedByMe: Boolean? = null
) {
    fun toDomain(districtName: String): CommunityPost {
        return CommunityPost(
            id = id,
            districtName = districtName,
            category = category,
            title = title,
            content = title,
            authorName = author,
            createdAtText = createdAt.toRelativeTimeTextFromIso(),
            likeCount = likeCount,
            commentCount = commentCount,
            isLiked = isLiked ?: liked ?: likedByMe ?: false
        )
    }
}
