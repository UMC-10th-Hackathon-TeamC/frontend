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
    val errorMessage: String? = null
)

class MyPageViewModel(
    private val repository: MyPageRepository = MyPageRepositoryProvider.create()
) : ViewModel() {
    var uiState by mutableStateOf(MyPageUiState())
        private set

    init {
        loadMyPage()
    }

    private fun loadMyPage() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            runCatching {
                val profile = repository.getMyProfile()

                // TODO: 실제 GPS 연결 전까지는 강남구 좌표를 임시로 사용
                val district = repository.getCurrentDistrict(
                    latitude = 37.5172,
                    longitude = 127.0473
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
                    mosquitoLevel = district?.level ?: MosquitoLevel.NORMAL
                )
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = throwable.message ?: "마이페이지 정보를 불러오지 못했어요."
                )
            }
        }
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
}
