package com.umc.hackathon.frontend.feature.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.umc.hackathon.frontend.feature.community.model.CommunityPost

private const val ALL_CATEGORY = "전체"

@Composable
fun CommunitySheet(
    districtName: String,
    posts: List<CommunityPost>,
    onWriteClick: () -> Unit,
    onCloseClick: () -> Unit,
    onCollapseClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ALL_CATEGORY) }
    val filteredPosts = remember(posts, selectedCategory) {
        if (selectedCategory == ALL_CATEGORY) {
            posts
        } else {
            posts.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CollapseHandle(onCollapseClick = onCollapseClick)
            CommunityHeader(districtName = districtName, onCloseClick = onCloseClick)
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 14.dp),
            color = Color(0xFFE6EAE3)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategoryClick = { selectedCategory = it }
            )

            if (filteredPosts.isEmpty()) {
                Text(
                    text = "아직 올라온 글이 없어요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                filteredPosts.forEach { post ->
                    CommunityPostItem(post = post)
                    HorizontalDivider(color = Color(0xFFE6EAE3))
                }
            }

            Spacer(modifier = Modifier.height(160.dp))
        }

        WriteButton(
            districtName = districtName,
            onWriteClick = onWriteClick
        )
    }
}

@Composable
private fun CollapseHandle(
    onCollapseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onCollapseClick)
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ChevronDownIcon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF2E6A44)
        )
        Text(
            text = "접기",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF2E6A44),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CommunityHeader(
    districtName: String,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = districtName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                LevelBadge(text = "높음")
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "모기 지수",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "72",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFD12A24),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "/ 100",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF0E8))
                .clickable(onClick = onCloseClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "x",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4E5A50)
            )
        }
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: String,
    onCategoryClick: (String) -> Unit
) {
    val categories = listOf(ALL_CATEGORY, "제보", "질문", "팁", "잡담")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        categories.forEach { category ->
            val selected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (selected) Color(0xFF2F794C) else Color(0xFFE9EEE7))
                    .clickable { onCategoryClick(category) }
                    .padding(horizontal = 13.dp, vertical = 7.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else Color(0xFF465349),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CommunityPostItem(
    post: CommunityPost
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF57705D)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = post.authorName.take(1),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    CategoryBadge(category = post.category)
                }
                Text(
                    text = post.createdAtText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ReactionText(icon = "♡", count = post.likeCount)
            ReactionText(icon = "○", count = post.commentCount)
        }
    }
}

@Composable
private fun WriteButton(
    districtName: String,
    onWriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF2F794C))
            .clickable(onClick = onWriteClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = EditIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
            Text(
                text = "$districtName 커뮤니티에 글 쓰기",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LevelBadge(
    text: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFDAD8))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFD12A24),
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun CategoryBadge(
    category: String
) {
    val backgroundColor = when (category) {
        "질문" -> Color(0xFFD8E7FF)
        "팁" -> Color(0xFFDDF3E4)
        "잡담" -> Color(0xFFE9DFFF)
        else -> Color(0xFFFFDAD8)
    }
    val contentColor = when (category) {
        "질문" -> Color(0xFF2E6FD8)
        "팁" -> Color(0xFF2F794C)
        "잡담" -> Color(0xFF6A45B8)
        else -> Color(0xFFD12A24)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ReactionText(
    icon: String,
    count: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val ChevronDownIcon: ImageVector = ImageVector.Builder(
    name = "ChevronDown",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(fill = SolidColor(Color.Black)) {
        moveTo(7.41f, 8.59f)
        lineTo(12f, 13.17f)
        lineTo(16.59f, 8.59f)
        lineTo(18f, 10f)
        lineTo(12f, 16f)
        lineTo(6f, 10f)
        close()
    }
}.build()

private val EditIcon: ImageVector = ImageVector.Builder(
    name = "Edit",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f
).apply {
    path(fill = SolidColor(Color.White)) {
        moveTo(3f, 17.25f)
        verticalLineTo(21f)
        horizontalLineTo(6.75f)
        lineTo(17.81f, 9.94f)
        lineTo(14.06f, 6.19f)
        lineTo(3f, 17.25f)
        close()
        moveTo(20.71f, 7.04f)
        curveTo(21.1f, 6.65f, 21.1f, 6.02f, 20.71f, 5.63f)
        lineTo(18.37f, 3.29f)
        curveTo(17.98f, 2.9f, 17.35f, 2.9f, 16.96f, 3.29f)
        lineTo(15.13f, 5.12f)
        lineTo(18.88f, 8.87f)
        lineTo(20.71f, 7.04f)
        close()
    }
}.build()
