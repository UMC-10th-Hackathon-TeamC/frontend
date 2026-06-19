package com.umc.hackathon.frontend.core.model

data class DistrictRanking(
    val updatedAt: String,
    val items: List<DistrictRankingItem>
)

data class DistrictRankingItem(
    val rank: Int,
    val id: Int,
    val districtName: String,
    val mosquitoIndex: Int,
    val level: MosquitoLevel
)
