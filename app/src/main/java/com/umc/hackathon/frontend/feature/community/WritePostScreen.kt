package com.umc.hackathon.frontend.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.hackathon.frontend.ui.theme.mogiMosquitoCardBackground
import com.umc.hackathon.frontend.ui.theme.mogiMutedGreenText
import com.umc.hackathon.frontend.ui.theme.mogiPrimaryGreen
import com.umc.hackathon.frontend.ui.theme.mogiTextSecondary
import com.umc.hackathon.frontend.ui.theme.mogiWritePostDistrictText
import com.umc.hackathon.frontend.ui.theme.mogiWritePostError
import com.umc.hackathon.frontend.ui.theme.surfaceContainerHighestDark

@Composable
fun WritePostRoute(
    districtId: Int,
    districtName: String,
    onBackClick: () -> Unit,
    viewModel: WritePostViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAuthorProfile()
    }

    val uiState = viewModel.uiState

    WritePostScreen(
        districtName = districtName,
        uiState = uiState,
        onTitleChange = viewModel::updateTitle,
        onContentChange = viewModel::updateContent,
        onBackClick = onBackClick,
        onSubmitClick = {
            viewModel.createPost(
                districtId = districtId,
                districtName = districtName,
                onSuccess = onBackClick
            )
        }
    )
}

@Composable
private fun WritePostScreen(
    districtName: String,
    uiState: WritePostUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(mogiMosquitoCardBackground)
            .padding(top = 48.dp)
    ) {
        WritePostTopBar(
            canSubmit = uiState.title.isNotBlank() &&
                uiState.content.isNotBlank() &&
                !uiState.isSubmitting,
            onBackClick = onBackClick,
            onSubmitClick = onSubmitClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        AuthorArea(
            districtName = districtName,
            authorName = uiState.authorName
        )

        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp),
            color = Color.White.copy(alpha = 0.08f)
        )

        TitleInput(
            title = uiState.title,
            onTitleChange = onTitleChange
        )

        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

        ContentInput(
            content = uiState.content,
            onContentChange = onContentChange,
            modifier = Modifier.weight(1f)
        )

        uiState.errorMessage?.let { errorMessage ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 8.dp),
                text = errorMessage,
                color = mogiWritePostError,
                style = MaterialTheme.typography.bodySmall
            )
        }

        WritePostBottomBar(contentLength = uiState.content.length)
    }
}

@Composable
private fun WritePostTopBar(
    canSubmit: Boolean,
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.clickable { onBackClick() },
            text = "‹",
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 34.sp,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(
                    if (canSubmit) mogiPrimaryGreen else surfaceContainerHighestDark.copy(alpha = 0.7f)
                )
                .clickable(enabled = canSubmit) { onSubmitClick() }
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text = "남기기",
                color = Color.White.copy(alpha = if (canSubmit) 1f else 0.72f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AuthorArea(
    districtName: String,
    authorName: String
) {
    val displayName = authorName.ifBlank { "모기맵유저" }
    val profileInitial = displayName.firstOrNull()?.toString() ?: "모"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(mogiPrimaryGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = profileInitial,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = displayName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${districtName}에 남기기",
                color = mogiWritePostDistrictText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TitleInput(
    title: String,
    onTitleChange: (String) -> Unit
) {
    BasicTextField(
        value = title,
        onValueChange = onTitleChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 16.dp),
        singleLine = true,
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        decorationBox = { innerTextField ->
            Box {
                if (title.isBlank()) {
                    Text(
                        text = "제목을 입력해주세요",
                        color = mogiTextSecondary.copy(alpha = 0.5f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun ContentInput(
    content: String,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = content,
        onValueChange = onContentChange,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 16.dp),
        textStyle = TextStyle(
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 15.sp,
            lineHeight = 26.sp
        ),
        decorationBox = { innerTextField ->
            Box {
                if (content.isBlank()) {
                    Text(
                        text = "욕설, 비난, 도배성 글을 남기면 영구적으로 활동이 제한될 수 있어요. 건강한 커뮤니티 문화를 함께 만들어가요.\n\n자세한 내용은 커뮤니티 서비스 이용규칙을 참고해주세요.",
                        color = mogiMutedGreenText.copy(alpha = 0.45f),
                        fontSize = 14.sp,
                        lineHeight = 26.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun WritePostBottomBar(
    contentLength: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(Color.Black.copy(alpha = 0.16f))
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(Color.White.copy(alpha = 0.1f))
                .clickable { }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "▧ 사진",
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = contentLength.toString(),
            color = mogiTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
