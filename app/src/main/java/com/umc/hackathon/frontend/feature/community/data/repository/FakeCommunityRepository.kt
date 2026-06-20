package com.umc.hackathon.frontend.feature.community.data.repository

import com.umc.hackathon.frontend.feature.community.model.CommunityPost

class FakeCommunityRepository : CommunityRepository {
    private companion object {
        const val MINUTE_IN_MILLIS = 60_000L
        const val HOUR_IN_MILLIS = 60L * MINUTE_IN_MILLIS
        const val DAY_IN_MILLIS = 24L * HOUR_IN_MILLIS

        val baseTimeMillis: Long = System.currentTimeMillis()

        var posts = listOf(
            CommunityPost(
                id = 1,
                districtName = "강남구",
                category = "팁",
                title = "레몬그라스 캔들이 효과 좋더라고요",
                content = "레몬그라스 캔들이 모기향보다 훨씬 효과 좋더라고요. 라벤더 오일 스프레이도 같이 쓰면 완벽합니다.",
                authorName = "방충전문가",
                createdAtText = "5시간 전",
                likeCount = 89,
                commentCount = 24,
                createdAtMillis = baseTimeMillis - 5L * HOUR_IN_MILLIS
            ),
            CommunityPost(
                id = 2,
                districtName = "강남구",
                category = "제보",
                title = "압구정 로데오 쪽 오늘 꽤 심하네요",
                content = "야외 테라스 카페를 가실 분은 기피제 필수!",
                authorName = "압구정러",
                createdAtText = "4시간 전",
                likeCount = 29,
                commentCount = 7,
                createdAtMillis = baseTimeMillis - 4L * HOUR_IN_MILLIS
            )
        )
    }

    override suspend fun getRecentPosts(districtName: String): List<CommunityPost> {
        return posts.filter {
            it.districtName == districtName
        }.map {
            it.withRelativeCreatedAtText()
        }
    }

    override suspend fun getPostsByDistrict(
        districtId: Int,
        districtName: String,
        cursor: Long?,
        limit: Int?
    ): List<CommunityPost> {
        return getRecentPosts(districtName).let { filteredPosts ->
            if (limit == null) filteredPosts else filteredPosts.take(limit)
        }
    }

    override suspend fun getPost(postId: Long): CommunityPost? {
        return posts.firstOrNull {
            it.id == postId
        }
    }

    override suspend fun createPost(
        districtId: Int,
        districtName: String,
        category: String,
        title: String,
        content: String,
        authorName: String
    ): CommunityPost {
        val nowMillis = System.currentTimeMillis()
        val post = CommunityPost(
            id = (posts.maxOfOrNull { it.id } ?: 0L) + 1L,
            districtName = districtName,
            category = category,
            title = title,
            content = content,
            authorName = authorName.ifBlank { "모기맵유저" },
            createdAtText = "방금 전",
            likeCount = 0,
            commentCount = 0,
            isMine = true,
            createdAtMillis = nowMillis
        )
        posts = listOf(post) + posts
        return post
    }

    override suspend fun updatePost(
        postId: Long,
        title: String?,
        content: String?
    ): CommunityPost? {
        val post = getPost(postId) ?: return null
        val updatedPost = post.copy(
            title = title ?: post.title,
            content = content ?: post.content
        )
        posts = posts.map {
            if (it.id == postId) updatedPost else it
        }
        return updatedPost
    }

    override suspend fun deletePost(postId: Long) {
        posts = posts.filterNot {
            it.id == postId
        }
    }

    override suspend fun likePost(postId: Long): Int {
        return updateLikeState(
            postId = postId,
            isLiked = true
        )
    }

    override suspend fun unlikePost(postId: Long): Int {
        return updateLikeState(
            postId = postId,
            isLiked = false
        )
    }

    private fun updateLikeState(
        postId: Long,
        isLiked: Boolean
    ): Int {
        var nextLikeCount = 0
        posts = posts.map { post ->
            if (post.id == postId) {
                val delta = when {
                    isLiked && !post.isLiked -> 1
                    !isLiked && post.isLiked -> -1
                    else -> 0
                }
                nextLikeCount = (post.likeCount + delta).coerceAtLeast(0)
                post.copy(
                    likeCount = nextLikeCount,
                    isLiked = isLiked
                )
            } else {
                post
            }
        }
        return nextLikeCount
    }

    private fun CommunityPost.withRelativeCreatedAtText(): CommunityPost {
        val createdAtMillis = createdAtMillis ?: return this
        return copy(
            createdAtText = createdAtMillis.toRelativeTimeText()
        )
    }

    private fun Long.toRelativeTimeText(): String {
        val diffMillis = (System.currentTimeMillis() - this).coerceAtLeast(0L)
        val minutes = diffMillis / MINUTE_IN_MILLIS
        val hours = diffMillis / HOUR_IN_MILLIS
        val days = diffMillis / DAY_IN_MILLIS

        return when {
            minutes < 1L -> "방금 전"
            hours < 1L -> "${minutes}분 전"
            hours < 24L -> "${hours}시간 전"
            else -> "${days}일 전"
        }
    }

}
