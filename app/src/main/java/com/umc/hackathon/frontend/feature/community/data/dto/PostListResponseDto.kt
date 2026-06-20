package com.umc.hackathon.frontend.feature.community.data.dto

data class PostListResponseDto(
    val posts: List<CommunityPostListItemDto>,
    val nextCursor: Long?
)
