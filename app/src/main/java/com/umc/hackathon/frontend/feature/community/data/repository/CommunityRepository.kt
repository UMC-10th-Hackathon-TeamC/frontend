package com.umc.hackathon.frontend.feature.community.data.repository

import com.umc.hackathon.frontend.feature.community.model.CommunityPost

interface CommunityRepository {
    suspend fun getRecentPosts(districtName: String): List<CommunityPost>
    suspend fun getPost(postId: Long): CommunityPost?
}
