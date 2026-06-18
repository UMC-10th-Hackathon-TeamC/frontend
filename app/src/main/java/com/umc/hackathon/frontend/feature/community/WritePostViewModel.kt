package com.umc.hackathon.frontend.feature.community

import androidx.lifecycle.ViewModel

data class WritePostUiState(
    val selectedCategory: String = "제보",
    val title: String = "",
    val content: String = ""
)

class WritePostViewModel : ViewModel() {
    val uiState = WritePostUiState()
}
