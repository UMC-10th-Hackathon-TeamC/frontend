package com.umc.hackathon.frontend.feature.community.data.api

import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.community.data.dto.CommunityPostDetailDto
import com.umc.hackathon.frontend.feature.community.data.dto.CreatePostRequestDto
import com.umc.hackathon.frontend.feature.community.data.dto.CreatePostResponseDto
import com.umc.hackathon.frontend.feature.community.data.dto.LikePostResponseDto
import com.umc.hackathon.frontend.feature.community.data.dto.PostListResponseDto
import com.umc.hackathon.frontend.feature.community.data.dto.UpdatePostRequestDto
import com.umc.hackathon.frontend.feature.community.data.dto.UpdatePostResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityApi {
    @GET("districts/{districtId}/posts")
    suspend fun getPostsByDistrict(
        @Path("districtId") districtId: Int,
        @Query("cursor") cursor: Long? = null,
        @Query("limit") limit: Int? = null
    ): ApiResponse<PostListResponseDto>

    @GET("posts/{postId}")
    suspend fun getPost(
        @Path("postId") postId: Long
    ): ApiResponse<CommunityPostDetailDto>

    @POST("posts")
    suspend fun createPost(
        @Body request: CreatePostRequestDto
    ): ApiResponse<CreatePostResponseDto>

    @PATCH("posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Long,
        @Body request: UpdatePostRequestDto
    ): ApiResponse<UpdatePostResponseDto>

    @DELETE("posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: Long
    ): ApiResponse<Unit>

    @POST("posts/{postId}/likes")
    suspend fun likePost(
        @Path("postId") postId: Long
    ): ApiResponse<LikePostResponseDto>

    @DELETE("posts/{postId}/likes")
    suspend fun unlikePost(
        @Path("postId") postId: Long
    ): ApiResponse<LikePostResponseDto>
}
