package com.umc.hackathon.frontend.feature.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umc.hackathon.frontend.feature.community.data.repository.CommunityRepository
import com.umc.hackathon.frontend.feature.community.data.repository.CommunityRepositoryProvider
import com.umc.hackathon.frontend.feature.mypage.data.repository.MyPageRepository
import com.umc.hackathon.frontend.feature.mypage.data.repository.MyPageRepositoryProvider
import kotlinx.coroutines.launch

data class WritePostUiState(
    val selectedCategory: String = "제보",
    val authorName: String = "",
    val title: String = "",
    val content: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)

class WritePostViewModel(
    private val communityRepository: CommunityRepository = CommunityRepositoryProvider.create(),
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
        uiState = uiState.copy(
            title = title,
            errorMessage = null
        )
    }

    fun updateContent(content: String) {
        uiState = uiState.copy(
            content = content,
            errorMessage = null
        )
    }

    fun createPost(
        districtId: Int,
        districtName: String,
        onSuccess: () -> Unit
    ) {
        if (uiState.isSubmitting) return
        if (uiState.title.isBlank() || uiState.content.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(
                isSubmitting = true,
                errorMessage = null
            )

            runCatching {
                communityRepository.createPost(
                    districtId = districtId,
                    districtName = districtName,
                    category = uiState.selectedCategory,
                    title = uiState.title,
                    content = uiState.content,
                    authorName = uiState.authorName.ifBlank { "모기맵유저" }
                )
            }.onSuccess {
                uiState = uiState.copy(isSubmitting = false)
                onSuccess()
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    isSubmitting = false,
                    errorMessage = throwable.message ?: "게시글 작성에 실패했어요."
                )
            }
        }
    }
}
