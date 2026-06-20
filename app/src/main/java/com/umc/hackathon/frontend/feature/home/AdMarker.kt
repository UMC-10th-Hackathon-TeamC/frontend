package com.umc.hackathon.frontend.feature.home

data class AdMarker(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val iconText: String = "🏠",
    val linkUrl: String? = null
)
