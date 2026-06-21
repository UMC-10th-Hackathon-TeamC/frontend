package com.umc.hackathon.frontend.feature.community.data.repository

import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.community.data.api.CommunityApi
import com.umc.hackathon.frontend.feature.community.data.dto.CreatePostRequestDto
import com.umc.hackathon.frontend.feature.community.data.dto.UpdatePostRequestDto
import com.umc.hackathon.frontend.feature.community.data.dto.toRelativeTimeTextFromIso
import com.umc.hackathon.frontend.feature.community.model.CommunityPost

class RemoteCommunityRepository(
    private val communityApi: CommunityApi
) : CommunityRepository {
    override suspend fun getRecentPosts(districtName: String): List<CommunityPost> {
        return emptyList()
    }

    override suspend fun getPostsByDistrict(
        districtId: Int,
        districtName: String,
        cursor: Long?,
        limit: Int?
    ): List<CommunityPost> {
        return communityApi.getPostsByDistrict(
            districtId = districtId,
            cursor = cursor,
            limit = limit
        ).requireData().posts.map {
            it.toDomain(districtName)
        }
    }

    override suspend fun getPost(postId: Long): CommunityPost? {
        return communityApi.getPost(postId)
            .requireData()
            .toDomain()
    }

    override suspend fun createPost(
        districtId: Int,
        districtName: String,
        category: String,
        title: String,
        content: String,
        authorName: String
    ): CommunityPost {
        val response = communityApi.createPost(
            CreatePostRequestDto(
                districtId = districtId,
                category = category,
                title = title,
                content = content
            )
        ).requireData()

        return CommunityPost(
            id = response.id,
            districtName = districtName,
            category = category,
            title = response.title,
            content = content,
            authorName = authorName,
            createdAtText = response.createdAt.toRelativeTimeTextFromIso(),
            likeCount = 0,
            commentCount = 0,
            isLiked = false,
            isMine = true
        )
    }

    override suspend fun updatePost(
        postId: Long,
        title: String?,
        content: String?
    ): CommunityPost? {
        communityApi.updatePost(
            postId = postId,
            request = UpdatePostRequestDto(
                title = title,
                content = content
            )
        ).requireSuccess()

        return getPost(postId)
    }

    override suspend fun deletePost(postId: Long) {
        communityApi.deletePost(postId).requireSuccess()
    }

    override suspend fun likePost(postId: Long): Int? {
        val response = communityApi.likePost(postId)
        response.requireSuccess()
        return response.data?.likeCount
    }

    override suspend fun unlikePost(postId: Long): Int? {
        val response = communityApi.unlikePost(postId)
        response.requireSuccess()
        return response.data?.likeCount
    }
}

private fun <T> ApiResponse<T>.requireData(): T {
    if (!success) {
        throw IllegalStateException(message)
    }

    return data ?: throw IllegalStateException("API response data is null.")
}

private fun <T> ApiResponse<T>.requireSuccess() {
    if (!success) {
        throw IllegalStateException(message)
    }
}
