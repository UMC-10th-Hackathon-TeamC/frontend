package com.umc.hackathon.frontend.feature.mypage

import androidx.lifecycle.ViewModel

data class MyPageUiState(
    val nickname: String = "모기맵유저",
    val email: String = "user@gmail.com",
    val districtName: String = "강남구",
    val mosquitoIndex: Int = 72
)

class MyPageViewModel : ViewModel() {
    val uiState = MyPageUiState()
}
