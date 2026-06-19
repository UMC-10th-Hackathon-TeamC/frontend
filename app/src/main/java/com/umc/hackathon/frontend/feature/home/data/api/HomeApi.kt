package com.umc.hackathon.frontend.feature.home.data.api

import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.home.data.dto.DistrictDetailDto
import com.umc.hackathon.frontend.feature.home.data.dto.DistrictRankingResponseDto
import com.umc.hackathon.frontend.feature.home.data.dto.DistrictsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeApi {
    @GET("districts")
    suspend fun getDistricts(): ApiResponse<DistrictsResponseDto>

    @GET("districts/{districtId}")
    suspend fun getDistrictDetail(
        @Path("districtId") districtId: Int
    ): ApiResponse<DistrictDetailDto>

    @GET("districts/ranking")
    suspend fun getDistrictRanking(): ApiResponse<DistrictRankingResponseDto>
}
