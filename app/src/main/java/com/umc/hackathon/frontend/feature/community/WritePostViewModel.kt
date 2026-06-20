package com.umc.hackathon.frontend.feature.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.hackathon.frontend.feature.mypage.data.repository.MyPageRepository
import com.umc.hackathon.frontend.feature.mypage.data.repository.MyPageRepositoryProvider
import kotlinx.coroutines.launch

data class WritePostUiState(
    val selectedCategory: String = "제보",
    val authorName: String = "",
    val title: String = "",
    val content: String = ""
)

class WritePostViewModel(
    private val myPageRepository: MyPageRepository = MyPageRepositoryProvider.create()
) : ViewModel() {
    var uiState by mutableStateOf(WritePostUiState())
        private set

    fun loadAuthorProfile() {
        viewModelScope.launch {
            runCatching {
                myPageRepository.getMyProfile()
            }.onSuccess { profile ->
                uiState = uiState.copy(
                    authorName = profile?.nickname.orEmpty()
                )
            }
        }
    }

    fun updateTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun updateContent(content: String) {
        uiState = uiState.copy(content = content)
    }
}
