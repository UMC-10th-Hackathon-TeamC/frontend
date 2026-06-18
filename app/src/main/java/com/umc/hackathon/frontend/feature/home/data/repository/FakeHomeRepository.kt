package com.umc.hackathon.frontend.feature.home.data.repository

import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex

class FakeHomeRepository : HomeRepository {
    private val districtIndexes = listOf(
        DistrictMosquitoIndex("서초구", 96, 37.4837, 127.0324),
        DistrictMosquitoIndex("동작구", 76, 37.5124, 126.9393),
        DistrictMosquitoIndex("강남구", 72, 37.5172, 127.0473),
        DistrictMosquitoIndex("마포구", 64, 37.5638, 126.9084),
        DistrictMosquitoIndex("영등포구", 59, 37.5264, 126.8962),
        DistrictMosquitoIndex("강동구", 58, 37.5301, 127.1238),
        DistrictMosquitoIndex("중랑구", 58, 37.6063, 127.0925),
        DistrictMosquitoIndex("금천구", 54, 37.4569, 126.8955),
        DistrictMosquitoIndex("관악구", 52, 37.4784, 126.9516),
        DistrictMosquitoIndex("구로구", 52, 37.4954, 126.8874),
        DistrictMosquitoIndex("강서구", 52, 37.5509, 126.8495),
        DistrictMosquitoIndex("송파구", 51, 37.5145, 127.1059),
        DistrictMosquitoIndex("성북구", 44, 37.5894, 127.0167),
        DistrictMosquitoIndex("은평구", 39, 37.6027, 126.9291),
        DistrictMosquitoIndex("광진구", 37, 37.5385, 127.0823),
        DistrictMosquitoIndex("종로구", 35, 37.5735, 126.9789),
        DistrictMosquitoIndex("동대문구", 33, 37.5744, 127.0396),
        DistrictMosquitoIndex("서대문구", 32, 37.5791, 126.9368),
        DistrictMosquitoIndex("강북구", 31, 37.6396, 127.0257),
        DistrictMosquitoIndex("도봉구", 30, 37.6688, 127.0471),
        DistrictMosquitoIndex("중구", 29, 37.5636, 126.9976),
        DistrictMosquitoIndex("노원구", 18, 37.6542, 127.0568),
        DistrictMosquitoIndex("성동구", 18, 37.5633, 127.0369),
        DistrictMosquitoIndex("용산구", 18, 37.5326, 126.9905),
        DistrictMosquitoIndex("양천구", 49, 37.5169, 126.8664)
    )

    override suspend fun getTodayDistrictIndexes(): List<DistrictMosquitoIndex> {
        return districtIndexes
    }

    override suspend fun getDistrictIndex(districtName: String): DistrictMosquitoIndex? {
        return districtIndexes.firstOrNull {
            it.districtName == districtName
        }
    }
}
