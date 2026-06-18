package com.umc.hackathon.frontend.feature.ranking

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RankingBottomSheet(
    districtIndexes: List<DistrictMosquitoIndex>,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val ranking = districtIndexes.sortedByDescending { it.mosquitoIndex }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val collapsedHeight = 320.dp
        val expandedHeight = maxHeight * 0.7f
        val sheetHeight = if (expanded) expandedHeight else collapsedHeight

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = sheetHeight, max = sheetHeight)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 36.dp, vertical = 18.dp)
        ) {
            Box( // 바텀시트 핸들 손잡이
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(48.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(100))
                    .background(Color(0xFFC4CCC2))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text( // 접기, 전체 랭킹보기
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { expanded = !expanded },
                text = if (expanded) "▽ 접기" else "△ 전체 랭킹 보기",
                color = Color(0xFF2F7047),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text( // 금일 표시
                text = todayText(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF747C72)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row( // 랭킹 제목 및 주의 칩
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expanded) "전체 랭킹" else "오늘의 모기 지수 TOP 3",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF151A15)
                )

                if (!expanded) {
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .background(Color(0xFFFFD9D6))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        text = "🦟 주의",
                        color = Color(0xFFD02020),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val visibleRanking = if (expanded) ranking else ranking.take(3)

            Column( // 축소일 땐 3개, 확대일 땐 전부
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (expanded) {
                            Modifier.verticalScroll(rememberScrollState())
                        } else {
                            Modifier
                        }
                    ),
                verticalArrangement = Arrangement.spacedBy(if (expanded) 0.dp else 12.dp)
            ) {
                visibleRanking.forEachIndexed { index, district ->
                    val rank = index + 1
                    if (expanded) {
                        ExpandedRankingRow(
                            rank = rank,
                            district = district
                        )
                    } else {
                        RankingRow(
                            rank = rank,
                            district = district
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingRow(
    rank: Int,
    district: DistrictMosquitoIndex
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(38.dp)
                .width(38.dp)
                .clip(CircleShape)
                .background(if (rank == 1) Color(0xFF2F7047) else Color(0xFF647064)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            modifier = Modifier.width(82.dp),
            text = district.districtName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF151A15)
        )

        Spacer(modifier = Modifier.width(8.dp))

        MosquitoIndexBar(
            index = district.mosquitoIndex,
            modifier = Modifier
                .width(152.dp)
                .height(8.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            modifier = Modifier.width(28.dp),
            text = district.mosquitoIndex.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = rankingProgressColor(district.mosquitoIndex)
        )
    }
}

@Composable
private fun ExpandedRankingRow(
    rank: Int,
    district: DistrictMosquitoIndex
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.width(46.dp),
                text = rank.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (rank <= 3) Color(0xFF2F7047) else Color(0xFF747C72)
            )

            Text(
                modifier = Modifier.weight(1f),
                text = district.districtName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF151A15)
            )

            MosquitoLevelChip(level = district.level)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                modifier = Modifier.width(32.dp),
                text = district.mosquitoIndex.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = rankingProgressColor(district.mosquitoIndex)
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE2E7DE)
        )
    }
}

@Composable
private fun MosquitoLevelChip(level: MosquitoLevel) {
    val backgroundColor = when (level) {
        MosquitoLevel.VERY_HIGH -> Color(0xFFFFD9D6)
        MosquitoLevel.HIGH -> Color(0xFFFFDEC2)
        MosquitoLevel.NORMAL -> Color(0xFFFFF0B8)
        MosquitoLevel.LOW -> Color(0xFFDDEEE3)
    }
    val textColor = when (level) {
        MosquitoLevel.VERY_HIGH -> Color(0xFFD02020)
        MosquitoLevel.HIGH -> Color(0xFFC25A20)
        MosquitoLevel.NORMAL -> Color(0xFFD8A213)
        MosquitoLevel.LOW -> Color(0xFF2F7047)
    }

    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        text = level.label,
        color = textColor,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun MosquitoIndexBar( // 막대그래프
    index: Int,
    modifier: Modifier = Modifier
) {
    val progress = (index.coerceIn(0, 100) / 100f)
    val progressColor = rankingProgressColor(index)

    Canvas(modifier = modifier) {
        val radius = size.height / 2f
        val progressWidth = size.width * progress

        // Track 없이 채워진 막대만 그려서 오른쪽에 작은 꼬리가 남지 않게 한다.
        drawRoundRect(
            color = progressColor,
            size = Size(width = progressWidth, height = size.height),
            cornerRadius = CornerRadius(radius, radius)
        )
    }
}

private fun todayText(): String {
    return SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA).format(Date())
}

private fun rankingProgressColor(index: Int): Color {
    return when {
        index >= 76 -> Color(0xFFD02020)
        index >= 51 -> Color(0xFFC25A20)
        index >= 26 -> Color(0xFFD8A213)
        else -> Color(0xFF2F7047)
    }
}
