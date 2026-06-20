package com.umc.hackathon.frontend.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.DistrictRanking
import com.umc.hackathon.frontend.feature.community.data.repository.CommunityRepository
import com.umc.hackathon.frontend.feature.community.data.repository.CommunityRepositoryProvider
import com.umc.hackathon.frontend.feature.community.model.CommunityPost
import com.umc.hackathon.frontend.feature.home.data.repository.HomeRepository
import com.umc.hackathon.frontend.feature.home.data.repository.HomeRepositoryProvider
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoggedIn: Boolean = false,
    val districtIndexes: List<DistrictMosquitoIndex> = emptyList(),
    val districtRanking: DistrictRanking? = null,
    val recentPosts: List<CommunityPost> = emptyList(),
    val selectedDistrict: String? = null,
    val isRankingSheetVisible: Boolean = false,
    val isDistrictSheetVisible: Boolean = false,
    val isCommunitySheetVisible: Boolean = false,
    val isLoginPromptVisible: Boolean = false
)

class HomeViewModel(
    private val homeRepository: HomeRepository = HomeRepositoryProvider.create(),
    private val communityRepository: CommunityRepository = CommunityRepositoryProvider.create()
) : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            runCatching {
                val districtIndexes = homeRepository.getTodayDistrictIndexes()
                val districtRanking = runCatching {
                    homeRepository.getDistrictRanking()
                }.getOrNull()

                districtIndexes to districtRanking
            }.onSuccess { (districtIndexes, districtRanking) ->
                uiState = uiState.copy(
                    districtIndexes = districtIndexes,
                    districtRanking = districtRanking
                )
            }
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
            val recentPosts = loadPosts(districtName)

            uiState = uiState.copy(
                selectedDistrict = districtName,
                recentPosts = recentPosts,
                isRankingSheetVisible = false,
                isDistrictSheetVisible = true,
                isCommunitySheetVisible = false,
                isLoginPromptVisible = false
            )
        }
    }

    fun refreshSelectedDistrictPosts() {
        val districtName = uiState.selectedDistrict ?: return

        viewModelScope.launch {
            uiState = uiState.copy(
                recentPosts = loadPosts(districtName)
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
            isCommunitySheetVisible = true,
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

    fun dismissLoginPrompt() {
        uiState = uiState.copy(
            isLoginPromptVisible = false
        )
    }

    //사용자가 로그인했는지 판단
    fun updateLoginState(isLoggedIn: Boolean) {
        uiState = uiState.copy(isLoggedIn = isLoggedIn)
    }

    private suspend fun loadPosts(districtName: String): List<CommunityPost> {
        val selectedDistrict = uiState.districtIndexes.firstOrNull {
            it.districtName == districtName
        }

        return if (selectedDistrict == null) {
            communityRepository.getRecentPosts(districtName)
        } else {
            communityRepository.getPostsByDistrict(
                districtId = selectedDistrict.id,
                districtName = selectedDistrict.districtName
            )
        }
    }
}
