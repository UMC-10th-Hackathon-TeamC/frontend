package com.umc.hackathon.frontend.feature.home.data.repository

import com.umc.hackathon.frontend.core.model.DistrictMosquitoDetail
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.DistrictRanking
import com.umc.hackathon.frontend.core.network.ApiResponse
import com.umc.hackathon.frontend.feature.home.data.api.HomeApi

class RemoteHomeRepository(
    private val homeApi: HomeApi
) : HomeRepository {
    override suspend fun getTodayDistrictIndexes(): List<DistrictMosquitoIndex> {
        return homeApi.getDistricts()
            .requireData()
            .districts
            .map { it.toDomain() }
    }

    override suspend fun getDistrictIndex(districtName: String): DistrictMosquitoIndex? {
        return getTodayDistrictIndexes().firstOrNull {
            it.districtName == districtName
        }
    }

    override suspend fun getDistrictDetail(districtId: Int): DistrictMosquitoDetail? {
        return homeApi.getDistrictDetail(districtId)
            .requireData()
            .toDomain()
    }

    override suspend fun getDistrictRanking(): DistrictRanking {
        return homeApi.getDistrictRanking()
            .requireData()
            .toDomain()
    }
}

private fun <T> ApiResponse<T>.requireData(): T {
    if (!success) {
        throw IllegalStateException(message)
    }
    return data ?: throw IllegalStateException("API response data is null.")
}
