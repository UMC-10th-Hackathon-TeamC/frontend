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

    fun loadProfile() {
        /* 로그인 직후 상단 프로필 카드에 필요한 사용자 정보만 조회 */
        viewModelScope.launch {
            runCatching {
                repository.getMyProfile()
            }.onSuccess { profile ->
                uiState = uiState.copy(
                    nickname = profile?.nickname.orEmpty(),
                    email = profile?.email.orEmpty(),
                    profileImageUrl = profile?.profileImageUrl,
                    errorMessage = null
                )
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    errorMessage = throwable.message ?: "프로필 정보를 불러오지 못했어요."
                )
            }
        }
    }

    fun loadMyPage(
        latitude: Double,
        longitude: Double,
        locationStatusText: String = "현재 위치 기준"
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            /* 마이페이지는 프로필 정보와 현재 위치의 지역 정보를 함께 보여줌 */
            val profileResult = runCatching {
                repository.getMyProfile()
            }
            val districtResult = runCatching {
                repository.getCurrentDistrict(
                    latitude = latitude,
                    longitude = longitude
                )
            }

            val profile = profileResult.getOrNull()
            val district = districtResult.getOrNull()

            /* 둘 중 하나라도 성공하면 가능한 정보만 화면에 반영 */
            if (profileResult.isSuccess || districtResult.isSuccess) {
                uiState = uiState.copy(
                    isLoading = false,
                    nickname = profile?.nickname.orEmpty(),
                    email = profile?.email.orEmpty(),
                    profileImageUrl = profile?.profileImageUrl,
                    districtName = district?.districtName.orEmpty(),
                    mosquitoIndex = district?.mosquitoIndex ?: 0,
                    mosquitoLevel = district?.level ?: MosquitoLevel.NORMAL,
                    locationStatusText = locationStatusText,
                    errorMessage = districtResult.exceptionOrNull()?.message
                )
            } else {
                val throwable = profileResult.exceptionOrNull() ?: districtResult.exceptionOrNull()
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = throwable?.message ?: "마이페이지 정보를 불러오지 못했어요."
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

    fun loadCurrentDistrict(
        latitude: Double,
        longitude: Double,
        locationStatusText: String = "GPS 기반 현재 위치"
    ) {
        /* 비로그인 상태에서는 프로필 없이 현재 지역 모기 지수만 조회 */
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            runCatching {
                repository.getCurrentDistrict(
                    latitude = latitude,
                    longitude = longitude
                )
            }.onSuccess { district ->
                uiState = uiState.copy(
                    isLoading = false,
                    districtName = district?.districtName.orEmpty(),
                    mosquitoIndex = district?.mosquitoIndex ?: 0,
                    mosquitoLevel = district?.level ?: MosquitoLevel.NORMAL,
                    locationStatusText = locationStatusText
                )
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "현재 위치의 모기 지수를 불러오지 못했어요."
                )
            }
        }
    }

    fun loadCurrentDistrictWithDefaultLocation() {
        loadCurrentDistrict(
            latitude = DEFAULT_LATITUDE,
            longitude = DEFAULT_LONGITUDE,
            locationStatusText = "기본 위치 기준"
        )
    }

    fun updateNickname(nickname: String) {
        /* 닉네임 수정 성공 시 변경된 닉네임만 UI 상태에 반영 */
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
        /* 서버 로그아웃이 끝나면 로컬에 저장된 토큰도 함께 삭제 */
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
