package com.umc.hackathon.frontend.feature.home.data.repository

import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.DistrictMosquitoDetail
import com.umc.hackathon.frontend.core.model.DistrictRanking

interface HomeRepository {
    suspend fun getTodayDistrictIndexes(): List<DistrictMosquitoIndex>
    suspend fun getDistrictIndex(districtName: String): DistrictMosquitoIndex?
    suspend fun getDistrictDetail(districtId: Int): DistrictMosquitoDetail?
    suspend fun getDistrictRanking(): DistrictRanking
}
