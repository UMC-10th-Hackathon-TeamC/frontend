package com.umc.hackathon.frontend.feature.mypage.model

import com.umc.hackathon.frontend.core.model.MosquitoLevel

data class MyDistrict(
    val id: Int,
    val districtName: String,
    val mosquitoIndex: Int,
    val level: MosquitoLevel
)