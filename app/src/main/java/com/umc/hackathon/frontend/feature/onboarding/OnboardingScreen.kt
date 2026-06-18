package com.umc.hackathon.frontend.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.hackathon.frontend.core.ui.PlaceholderScreen

@Composable
fun OnboardingRoute(
    onEnterHome: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    PlaceholderScreen(
        title = uiState.appName,
        description = uiState.subtitle,
        primaryButtonText = "Google로 계속하기",
        onPrimaryClick = onEnterHome,
        secondaryButtonText = "로그인 없이 이용하기",
        onSecondaryClick = onEnterHome
    )
}
