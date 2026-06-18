package com.umc.hackathon.frontend.feature.district

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import com.umc.hackathon.frontend.feature.community.model.CommunityPost

@Composable
fun DistrictInfoSheet(
    selectedDistrict: DistrictMosquitoIndex?,
    recentPosts: List<CommunityPost>,
    onCommunityClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val districtName = selectedDistrict?.districtName ?: "강남구"
    val mosquitoIndex = selectedDistrict?.mosquitoIndex ?: 72
    val level = selectedDistrict?.level ?: MosquitoLevel.HIGH

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 250.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SheetTopArea(onCommunityClick = onCommunityClick) // 상단 커뮤니티 보기

        DistrictSummaryArea(    // 지역구, 레벨칩, 모기지수, 바텀시트 닫기
            districtName = districtName,
            mosquitoIndex = mosquitoIndex,
            level = level,
            onCloseClick = onCloseClick
        )

        Divider(color = Color(0xFFE2E7DF)) // 구분선

        RecentPostsArea(recentPosts = recentPosts) // 게시물 표시

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
        Text(
            modifier = Modifier.clickable { onCommunityClick() },
            text = "△ 커뮤니티 보기",
            color = Color(0xFF2F7047),
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
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp, top = 28.dp, end = 36.dp, bottom = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = districtName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF151A15)
                )

                Spacer(modifier = Modifier.width(12.dp))

                LevelChip(level = level)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "모기 지수 ",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF747C72)
                )
                Text(
                    text = mosquitoIndex.toString(),
                    fontSize = 30.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = levelTextColor(level)
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                    text = "/ 100",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF747C72)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = Color(0xFFEFF2EC),
                    shape = CircleShape
                )
                .clickable { onCloseClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×",
                color = Color(0xFF4D554D),
                fontSize = 34.sp,
                lineHeight = 34.sp
            )
        }
    }
}

@Composable
private fun LevelChip(
    level: MosquitoLevel
) {
    Box(
        modifier = Modifier
            .background(
                color = levelChipBackgroundColor(level),
                shape = RoundedCornerShape(100)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = level.label,
            color = levelTextColor(level),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecentPostsArea(
    recentPosts: List<CommunityPost>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp, vertical = 24.dp)
    ) {
        Text(
            text = "최근 게시글",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF747C72)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (recentPosts.isEmpty()) {
            Text(
                text = "아직 게시글이 없어요",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF747C72)
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryChip(category = post.category)

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = post.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF30362F),
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
    return when (category) {
        "제보" -> ChipColors(
            background = Color(0xFFFFD9D6),
            content = Color(0xFFD02020)
        )
        "질문" -> ChipColors(
            background = Color(0xFFDCE7FF),
            content = Color(0xFF245BDB)
        )
        "팁" -> ChipColors(
            background = Color(0xFFD9F2DF),
            content = Color(0xFF237A3B)
        )
        else -> ChipColors(
            background = Color(0xFFEAEDE8),
            content = Color(0xFF4D564D)
        )
    }
}

private fun levelChipBackgroundColor(level: MosquitoLevel): Color {
    return when (level) {
        MosquitoLevel.LOW -> Color(0xFFDDEEDF)
        MosquitoLevel.NORMAL -> Color(0xFFFFE9A8)
        MosquitoLevel.HIGH -> Color(0xFFFFD9BF)
        MosquitoLevel.VERY_HIGH -> Color(0xFFFFD4D1)
    }
}

private fun levelTextColor(level: MosquitoLevel): Color {
    return when (level) {
        MosquitoLevel.LOW -> Color(0xFF2F7047)
        MosquitoLevel.NORMAL -> Color(0xFFD09A00)
        MosquitoLevel.HIGH -> Color(0xFFC25A20)
        MosquitoLevel.VERY_HIGH -> Color(0xFFD02020)
    }
}