package com.umc.hackathon.frontend.feature.mypage

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.hackathon.frontend.core.ui.PlaceholderScreen

@Composable
fun MyPageRoute(
    onBackClick: () -> Unit,
    viewModel: MyPageViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    PlaceholderScreen(
        title = "마이페이지",
        description = "${uiState.nickname}\n${uiState.email}\n내 지역: ${uiState.districtName} ${uiState.mosquitoIndex}",
        primaryButtonText = "뒤로가기",
        onPrimaryClick = onBackClick,
        secondaryButtonText = "로그아웃",
        onSecondaryClick = onBackClick
    )
}
