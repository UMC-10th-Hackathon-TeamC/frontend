package com.umc.hackathon.frontend.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.feature.community.data.repository.CommunityRepository
import com.umc.hackathon.frontend.feature.community.data.repository.FakeCommunityRepository
import com.umc.hackathon.frontend.feature.community.model.CommunityPost
import com.umc.hackathon.frontend.feature.home.data.repository.FakeHomeRepository
import com.umc.hackathon.frontend.feature.home.data.repository.HomeRepository
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoggedIn: Boolean = false,
    val districtIndexes: List<DistrictMosquitoIndex> = emptyList(),
    val recentPosts: List<CommunityPost> = emptyList(),
    val selectedDistrict: String? = null,
    val isRankingSheetVisible: Boolean = false,
    val isDistrictSheetVisible: Boolean = false,
    val isCommunitySheetVisible: Boolean = false,
    val isLoginPromptVisible: Boolean = false
)

class HomeViewModel(
    private val homeRepository: HomeRepository = FakeHomeRepository(),
    private val communityRepository: CommunityRepository = FakeCommunityRepository()
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            uiState = uiState.copy(
                districtIndexes = homeRepository.getTodayDistrictIndexes()
            )
        }
    }

    fun showRankingSheet() {
        uiState = uiState.copy(
            isRankingSheetVisible = true,
            isDistrictSheetVisible = false,
            isCommunitySheetVisible = false,
            isLoginPromptVisible = false
        )
    }

    fun showDistrictSheet(districtName: String) {
        viewModelScope.launch {
            uiState = uiState.copy(
                selectedDistrict = districtName,
                recentPosts = communityRepository.getRecentPosts(districtName),
                isRankingSheetVisible = false,
                isDistrictSheetVisible = true,
                isCommunitySheetVisible = false,
                isLoginPromptVisible = false
            )
        }
    }

    fun showCommunitySheet() {
        uiState = uiState.copy(
            isRankingSheetVisible = false,
            isDistrictSheetVisible = false,
            isCommunitySheetVisible = true,
            isLoginPromptVisible = false
        )
    }

    fun requestWrite(): Boolean {
        if (uiState.isLoggedIn) return true
        uiState = uiState.copy(
            isRankingSheetVisible = false,
            isDistrictSheetVisible = false,
            isCommunitySheetVisible = false,
            isLoginPromptVisible = true
        )
        return false
    }

    fun dismissSheets() {
        uiState = uiState.copy(
            isRankingSheetVisible = false,
            isDistrictSheetVisible = false,
            isCommunitySheetVisible = false,
            isLoginPromptVisible = false
        )
    }
}
