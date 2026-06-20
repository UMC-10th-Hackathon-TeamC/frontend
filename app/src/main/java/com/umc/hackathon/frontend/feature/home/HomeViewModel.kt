package com.umc.hackathon.frontend.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.hackathon.frontend.core.model.DistrictMosquitoDetail
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.DistrictRanking
import com.umc.hackathon.frontend.feature.community.data.repository.CommunityRepository
import com.umc.hackathon.frontend.feature.community.data.repository.CommunityRepositoryProvider
import com.umc.hackathon.frontend.feature.community.model.CommunityPost
import com.umc.hackathon.frontend.feature.home.data.repository.HomeRepository
import com.umc.hackathon.frontend.feature.home.data.repository.HomeRepositoryProvider
import com.umc.hackathon.frontend.feature.mypage.data.repository.MyPageRepository
import com.umc.hackathon.frontend.feature.mypage.data.repository.MyPageRepositoryProvider
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoggedIn: Boolean = false,
    val nickname: String = "",
    val profileImageUrl: String? = null,
    val districtIndexes: List<DistrictMosquitoIndex> = emptyList(),
    val districtRanking: DistrictRanking? = null,
    val selectedDistrictDetail: DistrictMosquitoDetail? = null,
    val recentPosts: List<CommunityPost> = emptyList(),
    val selectedDistrict: String? = null,
    val isRankingSheetVisible: Boolean = false,
    val isDistrictSheetVisible: Boolean = false,
    val isCommunitySheetVisible: Boolean = false,
    val isLoginPromptVisible: Boolean = false,
    val loginPromptPurpose: LoginPromptPurpose = LoginPromptPurpose.WRITE
)

enum class LoginPromptPurpose {
    WRITE,
    LIKE
}

class HomeViewModel(
    private val homeRepository: HomeRepository = HomeRepositoryProvider.create(),
    private val communityRepository: CommunityRepository = CommunityRepositoryProvider.create(),
    private val myPageRepository: MyPageRepository = MyPageRepositoryProvider.create()
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
            val selectedDistrict = uiState.districtIndexes.firstOrNull {
                it.districtName == districtName
            }
            val districtDetail = selectedDistrict?.let { district ->
                runCatching {
                    homeRepository.getDistrictDetail(district.id)
                }.getOrNull()
            }
            val recentPosts = loadPosts(districtName)

            uiState = uiState.copy(
                selectedDistrict = districtName,
                selectedDistrictDetail = districtDetail,
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
            isLoginPromptVisible = true,
            loginPromptPurpose = LoginPromptPurpose.WRITE
        )
        return false
    }

    fun togglePostLike(post: CommunityPost) {
        if (!uiState.isLoggedIn) {
            uiState = uiState.copy(
                isRankingSheetVisible = false,
                isDistrictSheetVisible = false,
                isCommunitySheetVisible = true,
                isLoginPromptVisible = true,
                loginPromptPurpose = LoginPromptPurpose.LIKE
            )
            return
        }

        val nextLiked = !post.isLiked
        updatePostLikeState(
            postId = post.id,
            isLiked = nextLiked,
            likeCount = if (nextLiked) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)
        )

        viewModelScope.launch {
            runCatching {
                if (nextLiked) {
                    communityRepository.likePost(post.id)
                } else {
                    communityRepository.unlikePost(post.id)
                }
            }.onSuccess { likeCount ->
                updatePostLikeState(
                    postId = post.id,
                    isLiked = nextLiked,
                    likeCount = likeCount
                )
            }.onFailure {
                updatePostLikeState(
                    postId = post.id,
                    isLiked = post.isLiked,
                    likeCount = post.likeCount
                )
            }
        }
    }

    fun deletePost(post: CommunityPost) {
        if (!post.isMine) return

        val previousPosts = uiState.recentPosts
        uiState = uiState.copy(
            recentPosts = uiState.recentPosts.filterNot { it.id == post.id }
        )

        viewModelScope.launch {
            runCatching {
                communityRepository.deletePost(post.id)
            }.onFailure {
                uiState = uiState.copy(recentPosts = previousPosts)
            }
        }
    }

    fun dismissSheets() {
        uiState = uiState.copy(
            selectedDistrictDetail = null,
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
        uiState = uiState.copy(
            isLoggedIn = isLoggedIn,
            nickname = if (isLoggedIn) uiState.nickname else "",
            profileImageUrl = if (isLoggedIn) uiState.profileImageUrl else null
        )

        if (isLoggedIn) {
            loadProfile()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            runCatching {
                myPageRepository.getMyProfile()
            }.onSuccess { profile ->
                uiState = uiState.copy(
                    nickname = profile?.nickname.orEmpty(),
                    profileImageUrl = profile?.profileImageUrl
                )
            }
        }
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

    private fun updatePostLikeState(
        postId: Long,
        isLiked: Boolean,
        likeCount: Int
    ) {
        uiState = uiState.copy(
            recentPosts = uiState.recentPosts.map { post ->
                if (post.id == postId) {
                    post.copy(
                        isLiked = isLiked,
                        likeCount = likeCount
                    )
                } else {
                    post
                }
            }
        )
    }
}
