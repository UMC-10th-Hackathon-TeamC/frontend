package com.umc.hackathon.frontend.feature.onboarding

import androidx.lifecycle.ViewModel

data class OnboardingUiState(
    val appName: String = "모기맵",
    val subtitle: String = "서울 실시간 모기 지수"
)

class OnboardingViewModel : ViewModel() {
    val uiState = OnboardingUiState()
}
