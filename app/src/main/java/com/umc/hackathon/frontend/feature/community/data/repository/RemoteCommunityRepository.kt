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
        /* 서버 목록 응답 DTO를 화면에서 쓰는 CommunityPost 모델로 변환 */
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
        /* 생성 응답에는 본문이 없어서 요청에 사용한 content와 authorName을 화면 모델에 함께 반영 */
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
        /* 수정 성공 응답 구조가 달라도 성공 여부만 확인한 뒤 상세 API로 최신 게시글을 다시 조회 */
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
