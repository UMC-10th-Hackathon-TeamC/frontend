package com.umc.hackathon.frontend.core.model

data class DistrictMosquitoDetail(
    val id: Int,
    val districtName: String,
    val mosquitoIndex: Int,
    val latitude: Double,
    val longitude: Double,
    val level: MosquitoLevel,
    val description: String,
    val updatedAt: String
)
