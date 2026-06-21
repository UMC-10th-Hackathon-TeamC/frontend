package com.umc.hackathon.frontend.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import com.umc.hackathon.frontend.feature.community.model.CommunityPost
import com.umc.hackathon.frontend.ui.theme.mogiAdBackground
import com.umc.hackathon.frontend.ui.theme.mogiAdIconBackground
import com.umc.hackathon.frontend.ui.theme.mogiCommunityAvatarBackground
import com.umc.hackathon.frontend.ui.theme.mogiDivider
import com.umc.hackathon.frontend.ui.theme.mogiMosquitoCardBackground
import com.umc.hackathon.frontend.ui.theme.mogiMutedGreenText
import com.umc.hackathon.frontend.ui.theme.mogiPrimaryGreen
import com.umc.hackathon.frontend.ui.theme.mogiProgressBlue
import com.umc.hackathon.frontend.ui.theme.mogiProgressGreen
import com.umc.hackathon.frontend.ui.theme.mogiProgressIndicator
import com.umc.hackathon.frontend.ui.theme.mogiProgressOrange
import com.umc.hackathon.frontend.ui.theme.mogiProgressRed
import com.umc.hackathon.frontend.ui.theme.mogiProgressYellow
import com.umc.hackathon.frontend.ui.theme.mogiSheetBackground
import com.umc.hackathon.frontend.ui.theme.mogiSortUnselectedBackground
import com.umc.hackathon.frontend.ui.theme.mogiTextPrimary
import com.umc.hackathon.frontend.ui.theme.mogiTextSecondary
import com.umc.hackathon.frontend.ui.theme.mogiWriteFieldBackground
import com.umc.hackathon.frontend.ui.theme.mogiWriteIconBackground

@Composable
fun CommunitySheet(
    districtName: String,
    mosquitoIndex: Int,
    level: MosquitoLevel,
    posts: List<CommunityPost>,
    onLikeClick: (CommunityPost) -> Unit,
    onEditClick: (CommunityPost) -> Unit,
    onDeleteClick: (CommunityPost) -> Unit,
    onWriteClick: () -> Unit,
    onCloseClick: () -> Unit,
    onCollapseClick: () -> Unit
) {
    var sortType by remember { mutableStateOf(CommunitySortType.LATEST) }
    val sortedPosts = when (sortType) {
        CommunitySortType.LATEST -> posts
        CommunitySortType.POPULAR -> posts.sortedByDescending { it.likeCount }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .background(mogiSheetBackground)
    ) {
        CollapseHandle(onCollapseClick = onCollapseClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            MosquitoIndexCard(
                districtName = districtName,
                mosquitoIndex = mosquitoIndex,
                level = level,
                onCloseClick = onCloseClick
            )

            CommunityTitleBar(
                districtName = districtName,
                selectedSortType = sortType,
                onSortTypeChange = { sortType = it }
            )

            if (sortedPosts.isEmpty()) {
                EmptyPostMessage()
            } else {
                sortedPosts.forEachIndexed { index, post ->
                    CommunityPostItem(
                        post = post,
                        onLikeClick = onLikeClick,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                    if (index != sortedPosts.lastIndex) {
                        HorizontalDivider(color = mogiDivider)
                    }
                }
            }

            AdCard()

            Spacer(modifier = Modifier.height(78.dp))
        }

        WritePrompt(
            districtName = districtName,
            onWriteClick = onWriteClick
        )
    }
}

@Composable
private fun CollapseHandle(
    onCollapseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.clickable { onCollapseClick() },
            text = "▽ 접기",
            color = mogiPrimaryGreen,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MosquitoIndexCard(
    districtName: String,
    mosquitoIndex: Int,
    level: MosquitoLevel,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(mogiMosquitoCardBackground)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "🦟  $districtName 모기 지수",
                    color = mogiMutedGreenText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { onCloseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "×",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 22.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mosquitoIndex.toString(),
                color = Color.White,
                fontSize = 58.sp,
                lineHeight = 58.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = levelActivityLabel(level),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            MosquitoIndexProgress(index = mosquitoIndex)

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = mosquitoIndexDescription(
                    districtName = districtName,
                    mosquitoIndex = mosquitoIndex,
                    level = level
                ),
                color = mogiMutedGreenText,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun MosquitoIndexProgress(
    index: Int
) {
    val progress = (index.coerceIn(0, 100) / 100f)
    val progressBrush = Brush.horizontalGradient(
        colors = listOf(
            mogiProgressBlue,
            mogiProgressGreen,
            mogiProgressYellow,
            mogiProgressOrange,
            mogiProgressRed
        )
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val indicatorOffset = maxWidth * progress - 7.dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(100))
                .background(progressBrush)
        )
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset.coerceAtLeast(0.dp))
                .size(14.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(mogiProgressIndicator)
            )
        }
    }
}

@Composable
private fun CommunityTitleBar(
    districtName: String,
    selectedSortType: CommunitySortType,
    onSortTypeChange: (CommunitySortType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, top = 26.dp, end = 32.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "$districtName 커뮤니티",
            color = mogiTextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        SortChip(
            text = "최신순",
            selected = selectedSortType == CommunitySortType.LATEST,
            onClick = { onSortTypeChange(CommunitySortType.LATEST) }
        )
        Spacer(modifier = Modifier.width(6.dp))
        SortChip(
            text = "인기순",
            selected = selectedSortType == CommunitySortType.POPULAR,
            onClick = { onSortTypeChange(CommunitySortType.POPULAR) }
        )
    }
}

@Composable
private fun SortChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(if (selected) mogiPrimaryGreen else mogiSortUnselectedBackground)
            .clickable { onClick() }
            .padding(horizontal = 13.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else mogiTextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

private enum class CommunitySortType {
    LATEST,
    POPULAR
}

@Composable
private fun EmptyPostMessage() {
    Text(
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
        text = "아직 올라온 글이 없어요.",
        style = MaterialTheme.typography.bodyMedium,
        color = mogiTextSecondary
    )
}

@Composable
private fun CommunityPostItem(
    post: CommunityPost,
    onLikeClick: (CommunityPost) -> Unit,
    onEditClick: (CommunityPost) -> Unit,
    onDeleteClick: (CommunityPost) -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isDeleteDialogVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 32.dp, vertical = 18.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(mogiCommunityAvatarBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = post.authorName.take(1),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = post.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    color = mogiTextPrimary,
                    fontWeight = FontWeight.Bold
                )

                if (post.isMine) {
                    Box {
                        Text(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { isMenuExpanded = true }
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            text = "⋯",
                            color = mogiTextSecondary,
                            fontSize = 22.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = "수정") },
                                onClick = {
                                    isMenuExpanded = false
                                    onEditClick(post)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = "삭제") },
                                onClick = {
                                    isMenuExpanded = false
                                    isDeleteDialogVisible = true
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = post.createdAtText,
                style = MaterialTheme.typography.bodyMedium,
                color = mogiTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = mogiTextPrimary,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.clickable { onLikeClick(post) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (post.isLiked) "♥" else "♡",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (post.isLiked) mogiPrimaryGreen else mogiTextSecondary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = post.likeCount.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (post.isLiked) mogiPrimaryGreen else mogiTextSecondary
                )
            }
        }
    }

    if (isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDeleteDialogVisible = false },
            title = {
                Text(text = "게시글 삭제")
            },
            text = {
                Text(text = "정말 삭제하시겠습니까?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleteDialogVisible = false
                        onDeleteClick(post)
                    }
                ) {
                    Text(text = "삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { isDeleteDialogVisible = false }
                ) {
                    Text(text = "취소")
                }
            }
        )
    }
}

@Composable
private fun AdCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 10.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFC9D8CF),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(mogiAdBackground)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(mogiAdIconBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🏠", fontSize = 23.sp)
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(mogiPrimaryGreen)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "광고",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "클린방역서비스",
                    color = mogiTextPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "서울 전 지역 당일 방문 · 친환경 방역",
                color = mogiTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(100))
                .background(mogiPrimaryGreen)
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Text(
                text = "무료 견적 받기",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WritePrompt(
    districtName: String,
    onWriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(mogiSheetBackground)
            .padding(start = 28.dp, end = 28.dp, top = 10.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(mogiWriteIconBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(100))
                .background(mogiWriteFieldBackground)
                .clickable { onWriteClick() }
                .padding(horizontal = 22.dp, vertical = 14.dp)
        ) {
            Text(
                text = "${districtName}에 글 남기기...",
                color = mogiPrimaryGreen,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun levelActivityLabel(level: MosquitoLevel): String {
    return when (level) {
        MosquitoLevel.VERY_HIGH -> "매우 활발"
        MosquitoLevel.HIGH -> "활발"
        MosquitoLevel.NORMAL -> "보통"
        MosquitoLevel.LOW -> "잠잠함"
    }
}

private fun mosquitoIndexDescription(
    districtName: String,
    mosquitoIndex: Int,
    level: MosquitoLevel
): String {
    return when (level) {
        MosquitoLevel.LOW -> "현재 ${districtName}의 모기지수는 $mosquitoIndex 수준으로 ${levelDescription(level)} 편입니다. 모기 활동이 적습니다."
        MosquitoLevel.NORMAL -> "현재 ${districtName}의 모기지수는 $mosquitoIndex 수준으로 ${levelDescription(level)} 편입니다. 평소처럼 활동하셔도 됩니다."
        MosquitoLevel.HIGH -> "현재 ${districtName}의 모기지수는 $mosquitoIndex 수준으로 ${levelDescription(level)} 편입니다. 기피제 사용을 권장합니다."
        MosquitoLevel.VERY_HIGH -> "현재 ${districtName}의 모기지수는 $mosquitoIndex 수준으로 ${levelDescription(level)} 편입니다. 외출 시 각별한 주의가 필요합니다."
    }
}

private fun levelDescription(level: MosquitoLevel): String {
    return when (level) {
        MosquitoLevel.LOW -> "낮은"
        MosquitoLevel.NORMAL -> "보통"
        MosquitoLevel.HIGH -> "높은"
        MosquitoLevel.VERY_HIGH -> "매우 높은"
    }
}
