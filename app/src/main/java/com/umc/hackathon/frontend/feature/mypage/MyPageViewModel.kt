package com.umc.hackathon.frontend.feature.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.hackathon.frontend.core.data.AuthTokenStore
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import com.umc.hackathon.frontend.feature.mypage.data.repository.MyPageRepository
import com.umc.hackathon.frontend.feature.mypage.data.repository.MyPageRepositoryProvider
import kotlinx.coroutines.launch

data class MyPageUiState(
    val isLoading: Boolean = false,
    val nickname: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val districtName: String = "",
    val mosquitoIndex: Int = 0,
    val mosquitoLevel: MosquitoLevel = MosquitoLevel.NORMAL,
    val locationStatusText: String = "GPS 확인 중...",
    val errorMessage: String? = null
)

class MyPageViewModel(
    private val repository: MyPageRepository = MyPageRepositoryProvider.create()
) : ViewModel() {
    var uiState by mutableStateOf(MyPageUiState())
        private set

    fun loadMyPage(
        latitude: Double,
        longitude: Double,
        locationStatusText: String = "현재 위치 기준"
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            runCatching {
                val profile = repository.getMyProfile()
                val district = repository.getCurrentDistrict(
                    latitude = latitude,
                    longitude = longitude
                )

                profile to district
            }.onSuccess { (profile, district) ->
                uiState = uiState.copy(
                    isLoading = false,
                    nickname = profile?.nickname.orEmpty(),
                    email = profile?.email.orEmpty(),
                    profileImageUrl = profile?.profileImageUrl,
                    districtName = district?.districtName.orEmpty(),
                    mosquitoIndex = district?.mosquitoIndex ?: 0,
                    mosquitoLevel = district?.level ?: MosquitoLevel.NORMAL,
                    locationStatusText = locationStatusText
                )
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "마이페이지 정보를 불러오지 못했어요."
                )
            }
        }
    }

    fun loadMyPageWithDefaultLocation() {
        loadMyPage(
            latitude = DEFAULT_LATITUDE,
            longitude = DEFAULT_LONGITUDE,
            locationStatusText = "기본 위치 기준"
        )
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            runCatching {
                repository.updateNickname(nickname)
            }.onSuccess { profile ->
                uiState = uiState.copy(
                    nickname = profile?.nickname.orEmpty()
                )
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    errorMessage = throwable.message ?: "닉네임 수정에 실패했어요."
                )
            }
        }
    }

    //로그아웃하면 토큰 삭제
    fun logout(
        authTokenStore: AuthTokenStore,
        onLoggedOut: () -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                repository.logout()
                authTokenStore.clearTokens()
            }.onSuccess {
                onLoggedOut()
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    errorMessage = throwable.message ?: "로그아웃에 실패했어요."
                )
            }
        }
    }

    companion object {
        private const val DEFAULT_LATITUDE = 37.5172
        private const val DEFAULT_LONGITUDE = 127.0473
    }
}
