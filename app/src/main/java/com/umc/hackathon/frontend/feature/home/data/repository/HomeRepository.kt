package com.umc.hackathon.frontend.feature.home.data.repository

import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex

interface HomeRepository {
    suspend fun getTodayDistrictIndexes(): List<DistrictMosquitoIndex>
    suspend fun getDistrictIndex(districtName: String): DistrictMosquitoIndex?
}
