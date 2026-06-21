package com.umc.hackathon.frontend.feature.community.data.repository

import com.umc.hackathon.frontend.feature.community.model.CommunityPost

interface CommunityRepository {
    suspend fun getRecentPosts(districtName: String): List<CommunityPost>

    suspend fun getPostsByDistrict(
        districtId: Int,
        districtName: String,
        cursor: Long? = null,
        limit: Int? = null
    ): List<CommunityPost>

    suspend fun getPost(postId: Long): CommunityPost?

    suspend fun createPost(
        districtId: Int,
        districtName: String,
        category: String,
        title: String,
        content: String,
        authorName: String
    ): CommunityPost

    suspend fun updatePost(
        postId: Long,
        title: String?,
        content: String?
    ): CommunityPost?

    suspend fun deletePost(postId: Long)

    suspend fun likePost(postId: Long): Int?

    suspend fun unlikePost(postId: Long): Int?
}
