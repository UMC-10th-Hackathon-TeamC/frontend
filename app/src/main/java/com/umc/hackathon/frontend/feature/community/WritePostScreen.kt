package com.umc.hackathon.frontend.feature.community

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.hackathon.frontend.core.ui.PlaceholderScreen

@Composable
fun WritePostRoute(
    districtName: String,
    onBackClick: () -> Unit,
    viewModel: WritePostViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    PlaceholderScreen(
        title = "$districtName 에 남기기",
        description = "카테고리: ${uiState.selectedCategory}\n제목/내용 입력 UI가 들어갈 자리",
        primaryButtonText = "남기기",
        onPrimaryClick = onBackClick,
        secondaryButtonText = "뒤로가기",
        onSecondaryClick = onBackClick
    )
}
