package com.umc.hackathon.frontend.feature.home.data.dto

import com.umc.hackathon.frontend.core.model.DistrictMosquitoDetail
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.DistrictRanking
import com.umc.hackathon.frontend.core.model.DistrictRankingItem
import com.umc.hackathon.frontend.core.model.toMosquitoLevel
import kotlin.math.roundToInt

data class MosquitoIndexDto(
    val id: Int,
    val name: String,
    val mosquitoIndex: Double,
    val latitude: Double,
    val longitude: Double,
    val level: String
) {
    fun toDomain(): DistrictMosquitoIndex {
        return DistrictMosquitoIndex(
            id = id,
            districtName = name,
            mosquitoIndex = mosquitoIndex.roundToInt(),
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
    val mosquitoIndex: Double,
    val level: String,
    val description: String,
    val updatedAt: String
) {
    fun toDomain(): DistrictMosquitoDetail {
        return DistrictMosquitoDetail(
            id = id,
            districtName = name,
            mosquitoIndex = mosquitoIndex.roundToInt(),
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
    val mosquitoIndex: Double,
    val level: String
) {
    fun toDomain(): DistrictRankingItem {
        return DistrictRankingItem(
            rank = rank,
            id = id,
            districtName = name,
            mosquitoIndex = mosquitoIndex.roundToInt(),
            level = level.toMosquitoLevel()
        )
    }
}
