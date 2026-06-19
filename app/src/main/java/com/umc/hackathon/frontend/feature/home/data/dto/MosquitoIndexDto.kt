package com.umc.hackathon.frontend.feature.home.data.dto

import com.umc.hackathon.frontend.core.model.DistrictMosquitoDetail
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.DistrictRanking
import com.umc.hackathon.frontend.core.model.DistrictRankingItem
import com.umc.hackathon.frontend.core.model.toMosquitoLevel

data class MosquitoIndexDto(
    val id: Int,
    val name: String,
    val mosquitoIndex: Int,
    val latitude: Double,
    val longitude: Double,
    val level: String
) {
    fun toDomain(): DistrictMosquitoIndex {
        return DistrictMosquitoIndex(
            id = id,
            districtName = name,
            mosquitoIndex = mosquitoIndex,
            latitude = latitude,
            longitude = longitude,
            level = level.toMosquitoLevel()
        )
    }
}

data class DistrictsResponseDto(
    val districts: List<MosquitoIndexDto>
)

data class DistrictDetailDto(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val mosquitoIndex: Int,
    val level: String,
    val description: String,
    val updatedAt: String
) {
    fun toDomain(): DistrictMosquitoDetail {
        return DistrictMosquitoDetail(
            id = id,
            districtName = name,
            mosquitoIndex = mosquitoIndex,
            latitude = latitude,
            longitude = longitude,
            level = level.toMosquitoLevel(),
            description = description,
            updatedAt = updatedAt
        )
    }
}

data class DistrictRankingResponseDto(
    val updatedAt: String,
    val ranking: List<DistrictRankingItemDto>
) {
    fun toDomain(): DistrictRanking {
        return DistrictRanking(
            updatedAt = updatedAt,
            items = ranking.map { it.toDomain() }
        )
    }
}

data class DistrictRankingItemDto(
    val rank: Int,
    val id: Int,
    val name: String,
    val mosquitoIndex: Int,
    val level: String
) {
    fun toDomain(): DistrictRankingItem {
        return DistrictRankingItem(
            rank = rank,
            id = id,
            districtName = name,
            mosquitoIndex = mosquitoIndex,
            level = level.toMosquitoLevel()
        )
    }
}
