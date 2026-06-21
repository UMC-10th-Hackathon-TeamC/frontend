package com.umc.hackathon.frontend.feature.district

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.hackathon.frontend.core.model.DistrictMosquitoDetail
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import com.umc.hackathon.frontend.feature.community.model.CommunityPost
import com.umc.hackathon.frontend.ui.theme.mogiDefaultChipBackground
import com.umc.hackathon.frontend.ui.theme.mogiDefaultChipContent
import com.umc.hackathon.frontend.ui.theme.mogiDivider
import com.umc.hackathon.frontend.ui.theme.mogiMosquitoCardBackground
import com.umc.hackathon.frontend.ui.theme.mogiMutedGreenText
import com.umc.hackathon.frontend.ui.theme.mogiPostTitle
import com.umc.hackathon.frontend.ui.theme.mogiPrimaryGreen
import com.umc.hackathon.frontend.ui.theme.mogiProgressBlue
import com.umc.hackathon.frontend.ui.theme.mogiProgressGreen
import com.umc.hackathon.frontend.ui.theme.mogiProgressIndicator
import com.umc.hackathon.frontend.ui.theme.mogiProgressOrange
import com.umc.hackathon.frontend.ui.theme.mogiProgressRed
import com.umc.hackathon.frontend.ui.theme.mogiProgressYellow
import com.umc.hackathon.frontend.ui.theme.mogiQuestionChipBackground
import com.umc.hackathon.frontend.ui.theme.mogiQuestionChipContent
import com.umc.hackathon.frontend.ui.theme.mogiReportChipBackground
import com.umc.hackathon.frontend.ui.theme.mogiReportChipContent
import com.umc.hackathon.frontend.ui.theme.mogiTextSecondary
import com.umc.hackathon.frontend.ui.theme.mogiTipChipBackground
import com.umc.hackathon.frontend.ui.theme.mogiTipChipContent

@Composable
fun DistrictInfoSheet(
    selectedDistrict: DistrictMosquitoIndex?,
    selectedDistrictDetail: DistrictMosquitoDetail?,
    recentPosts: List<CommunityPost>,
    onCommunityClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    /* 상세 API 값이 있으면 우선 사용하고, 없으면 지도 목록의 기본 값을 사용 */
    val districtName = selectedDistrictDetail?.districtName ?: selectedDistrict?.districtName ?: "강남구"
    val mosquitoIndex = selectedDistrictDetail?.mosquitoIndex ?: selectedDistrict?.mosquitoIndex ?: 72
    val level = selectedDistrictDetail?.level ?: selectedDistrict?.level ?: MosquitoLevel.HIGH
    val description = selectedDistrictDetail?.description

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 250.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SheetTopArea(onCommunityClick = onCommunityClick)

        DistrictSummaryArea(
            districtName = districtName,
            mosquitoIndex = mosquitoIndex,
            level = level,
            description = description,
            onCloseClick = onCloseClick
        )

        Divider(color = mogiDivider)

        RecentPostsArea(recentPosts = recentPosts)
    }
}

@Composable
private fun SheetTopArea(
    onCommunityClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* 접힌 지역 정보 시트에서 커뮤니티 전체 시트로 전환 */
        Text(
            modifier = Modifier.clickable { onCommunityClick() },
            text = "△ 커뮤니티 보기",
            color = mogiPrimaryGreen,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DistrictSummaryArea(
    districtName: String,
    mosquitoIndex: Int,
    level: MosquitoLevel,
    description: String?,
    onCloseClick: () -> Unit
) {
    /* 선택된 지역의 모기 지수와 단계 설명을 카드 형태로 표시 */
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 22.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(mogiMosquitoCardBackground)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "🦟 $districtName 모기 지수",
                    color = mogiMutedGreenText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .clickable { onCloseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "×",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 20.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mosquitoIndex.toString(),
                color = Color.White,
                fontSize = 52.sp,
                lineHeight = 52.sp,
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
                text = description?.takeIf { it.isNotBlank() } ?: mosquitoIndexDescription(
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
    /* 0~100 모기 지수를 색상 바 위 위치 값으로 변환 */
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
private fun RecentPostsArea(
    recentPosts: List<CommunityPost>
) {
    /* 지역 정보 시트에서는 최근 게시글 중 일부만 미리보기로 노출 */
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "최근 게시글",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = mogiTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (recentPosts.isEmpty()) {
            Text(
                text = "아직 게시글이 없어요",
                style = MaterialTheme.typography.titleMedium,
                color = mogiTextSecondary
            )
        } else {
            recentPosts.take(3).forEach { post ->
                RecentPostRow(post = post)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RecentPostRow(
    post: CommunityPost
) {
    /* 좁은 시트 안에서 글 내용이 길어지지 않도록 앞부분만 표시 */
    val previewContent = if (post.content.length > 18) {
        post.content.take(18) + "..."
    } else {
        post.content
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = previewContent,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = mogiPostTitle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategoryChip(
    category: String
) {
    val colors = categoryChipColors(category)

    Box(
        modifier = Modifier
            .background(
                color = colors.background,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = category,
            color = colors.content,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class ChipColors(
    val background: Color,
    val content: Color
)

private fun categoryChipColors(category: String): ChipColors {
    /* 게시글 카테고리에 따라 칩 색상을 다르게 적용 */
    return when (category) {
        "제보" -> ChipColors(
            background = mogiReportChipBackground,
            content = mogiReportChipContent
        )
        "질문" -> ChipColors(
            background = mogiQuestionChipBackground,
            content = mogiQuestionChipContent
        )
        "팁" -> ChipColors(
            background = mogiTipChipBackground,
            content = mogiTipChipContent
        )
        else -> ChipColors(
            background = mogiDefaultChipBackground,
            content = mogiDefaultChipContent
        )
    }
}

private fun levelActivityLabel(level: MosquitoLevel): String {
    return when (level) {
        MosquitoLevel.LOW -> "잠잠함"
        MosquitoLevel.NORMAL -> "보통"
        MosquitoLevel.HIGH -> "활발"
        MosquitoLevel.VERY_HIGH -> "매우 활발"
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
