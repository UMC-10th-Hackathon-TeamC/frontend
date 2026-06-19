package com.umc.hackathon.frontend.feature.home.data.repository

import com.umc.hackathon.frontend.core.model.DistrictMosquitoDetail
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.DistrictRanking
import com.umc.hackathon.frontend.core.model.DistrictRankingItem

class FakeHomeRepository : HomeRepository {
    private val districtIndexes = listOf(
        DistrictMosquitoIndex("서초구", 96, 37.4837, 127.0324, id = 1),
        DistrictMosquitoIndex("동작구", 76, 37.5124, 126.9393, id = 2),
        DistrictMosquitoIndex("강남구", 72, 37.5172, 127.0473, id = 3),
        DistrictMosquitoIndex("마포구", 64, 37.5638, 126.9084, id = 4),
        DistrictMosquitoIndex("영등포구", 59, 37.5264, 126.8962, id = 5),
        DistrictMosquitoIndex("강동구", 58, 37.5301, 127.1238, id = 6),
        DistrictMosquitoIndex("중랑구", 58, 37.6063, 127.0925, id = 7),
        DistrictMosquitoIndex("금천구", 54, 37.4569, 126.8955, id = 8),
        DistrictMosquitoIndex("관악구", 52, 37.4784, 126.9516, id = 9),
        DistrictMosquitoIndex("구로구", 52, 37.4954, 126.8874, id = 10),
        DistrictMosquitoIndex("강서구", 52, 37.5509, 126.8495, id = 11),
        DistrictMosquitoIndex("송파구", 51, 37.5145, 127.1059, id = 12),
        DistrictMosquitoIndex("양천구", 49, 37.5169, 126.8664, id = 13),
        DistrictMosquitoIndex("성북구", 44, 37.5894, 127.0167, id = 14),
        DistrictMosquitoIndex("은평구", 39, 37.6027, 126.9291, id = 15),
        DistrictMosquitoIndex("광진구", 37, 37.5385, 127.0823, id = 16),
        DistrictMosquitoIndex("종로구", 35, 37.5735, 126.9789, id = 17),
        DistrictMosquitoIndex("동대문구", 33, 37.5744, 127.0396, id = 18),
        DistrictMosquitoIndex("서대문구", 32, 37.5791, 126.9368, id = 19),
        DistrictMosquitoIndex("강북구", 31, 37.6396, 127.0257, id = 20),
        DistrictMosquitoIndex("도봉구", 30, 37.6688, 127.0471, id = 21),
        DistrictMosquitoIndex("중구", 29, 37.5636, 126.9976, id = 22),
        DistrictMosquitoIndex("노원구", 18, 37.6542, 127.0568, id = 23),
        DistrictMosquitoIndex("성동구", 18, 37.5633, 127.0369, id = 24),
        DistrictMosquitoIndex("용산구", 18, 37.5326, 126.9905, id = 25)
    )

    override suspend fun getTodayDistrictIndexes(): List<DistrictMosquitoIndex> {
        return districtIndexes
    }

    override suspend fun getDistrictIndex(districtName: String): DistrictMosquitoIndex? {
        return districtIndexes.firstOrNull {
            it.districtName == districtName
        }
    }

    override suspend fun getDistrictDetail(districtId: Int): DistrictMosquitoDetail? {
        return districtIndexes.firstOrNull { it.id == districtId }?.let { district ->
            DistrictMosquitoDetail(
                id = district.id,
                districtName = district.districtName,
                mosquitoIndex = district.mosquitoIndex,
                latitude = district.latitude,
                longitude = district.longitude,
                level = district.level,
                description = "야외 활동 시 모기 기피제를 사용하세요.",
                updatedAt = "2026-05-31T00:00:00.000Z"
            )
        }
    }

    override suspend fun getDistrictRanking(): DistrictRanking {
        return DistrictRanking(
            updatedAt = "2026-05-31T00:00:00.000Z",
            items = districtIndexes
                .sortedByDescending { it.mosquitoIndex }
                .mapIndexed { index, district ->
                    DistrictRankingItem(
                        rank = index + 1,
                        id = district.id,
                        districtName = district.districtName,
                        mosquitoIndex = district.mosquitoIndex,
                        level = district.level
                    )
                }
        )
    }
}
