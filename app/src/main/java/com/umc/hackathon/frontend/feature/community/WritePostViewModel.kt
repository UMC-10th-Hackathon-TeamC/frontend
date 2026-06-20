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
    val content: String = "",
    val isSubmitting: Boolean = false,
    val isEditing: Boolean = false,
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

    fun loadPostForEdit(postId: Long) {
        if (postId <= 0L) return

        viewModelScope.launch {
            runCatching {
                communityRepository.getPost(postId)
            }.onSuccess { post ->
                post ?: return@onSuccess
                uiState = uiState.copy(
                    selectedCategory = post.category,
                    authorName = post.authorName,
                    content = post.content,
                    isEditing = true,
                    errorMessage = null
                )
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    isEditing = true,
                    errorMessage = throwable.message ?: "게시글을 불러오지 못했어요."
                )
            }
        }
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
        if (uiState.content.isBlank()) return

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
                    title = "",  // 명세상 title 필드가 필요해 빈 문자열로 전송
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

    fun updatePost(
        postId: Long,
        onSuccess: () -> Unit
    ) {
        if (uiState.isSubmitting) return
        if (uiState.content.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(
                isSubmitting = true,
                errorMessage = null
            )

            runCatching {
                communityRepository.updatePost(
                    postId = postId,
                    title = "",
                    content = uiState.content
                )
            }.onSuccess {
                uiState = uiState.copy(isSubmitting = false)
                onSuccess()
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    isSubmitting = false,
                    errorMessage = throwable.message ?: "게시글 수정에 실패했어요."
                )
            }
        }
    }
}
