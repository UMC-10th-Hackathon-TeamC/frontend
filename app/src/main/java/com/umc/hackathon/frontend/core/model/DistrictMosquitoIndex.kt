package com.umc.hackathon.frontend.core.model

data class DistrictMosquitoIndex(
    val districtName: String,
    val mosquitoIndex: Int,
    val latitude: Double,
    val longitude: Double,
    val level: MosquitoLevel = mosquitoIndex.toMosquitoLevel()
)
