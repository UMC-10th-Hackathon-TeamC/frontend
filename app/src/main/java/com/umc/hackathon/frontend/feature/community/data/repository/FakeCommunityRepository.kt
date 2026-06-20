package com.umc.hackathon.frontend.feature.community.data.repository

import com.umc.hackathon.frontend.feature.community.model.CommunityPost

class FakeCommunityRepository : CommunityRepository {
    private var posts = listOf(
        CommunityPost(
            id = 1,
            districtName = "강남구",
            category = "팁",
            title = "레몬그라스 캔들이 효과 좋더라고요",
            content = "레몬그라스 캔들이 모기향보다 훨씬 효과 좋더라고요. 라벤더 오일 스프레이도 같이 쓰면 완벽합니다.",
            authorName = "방충전문가",
            createdAtText = "5시간 전",
            likeCount = 89,
            commentCount = 24
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
            commentCount = 7
        )
    )

    override suspend fun getRecentPosts(districtName: String): List<CommunityPost> {
        return posts.filter {
            it.districtName == districtName
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
        content: String
    ): CommunityPost {
        val post = CommunityPost(
            id = (posts.maxOfOrNull { it.id } ?: 0L) + 1L,
            districtName = districtName,
            category = category,
            title = title,
            content = content,
            authorName = "모기맵유저",
            createdAtText = "방금 전",
            likeCount = 0,
            commentCount = 0
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
        return updateLikeCount(
            postId = postId,
            delta = 1
        )
    }

    override suspend fun unlikePost(postId: Long): Int {
        return updateLikeCount(
            postId = postId,
            delta = -1
        )
    }

    private fun updateLikeCount(
        postId: Long,
        delta: Int
    ): Int {
        var nextLikeCount = 0
        posts = posts.map { post ->
            if (post.id == postId) {
                nextLikeCount = (post.likeCount + delta).coerceAtLeast(0)
                post.copy(likeCount = nextLikeCount)
            } else {
                post
            }
        }
        return nextLikeCount
    }
}
