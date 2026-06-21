package com.umc.hackathon.frontend.feature.ranking

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.DistrictRanking
import com.umc.hackathon.frontend.core.model.DistrictRankingItem
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RankingBottomSheet(
    districtIndexes: List<DistrictMosquitoIndex>,
    districtRanking: DistrictRanking?,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    /* 랭킹 API 값이 없으면 지도에 표시 중인 모기 지수 목록으로 TOP 순위를 임시 계산 */
    val ranking = districtRanking?.items
        ?.takeIf { it.isNotEmpty() }
        ?: districtIndexes
            .sortedByDescending { it.mosquitoIndex }
            .mapIndexed { index, district ->
                DistrictRankingItem(
                    rank = index + 1,
                    id = district.id,
                    districtName = district.districtName,
                    mosquitoIndex = district.mosquitoIndex,
                    level = district.level
                )
            }
    val rankingDateText = todayText()

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        /* 접힌 상태는 TOP3만 보이고, 펼친 상태는 화면 높이의 70%까지 확장 */
        val collapsedHeight = 315.dp
        val expandedHeight = maxHeight * 0.7f
        val sheetHeight = if (expanded) expandedHeight else collapsedHeight
        /* 상위 3개는 카드로 4위 이하는 펼친 상태에서 리스트로 표시 */
        val topThree = ranking.take(3)
        val lowerRanking = ranking.drop(3)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = sheetHeight, max = sheetHeight)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 34.dp, vertical = 18.dp)
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
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text( // 금일 표시
                text = rankingDateText,
                style = MaterialTheme.typography.bodySmall,
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF151A15)
                )

                if (!expanded) {
                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .background(Color(0xFFFFD9D6))
                            .padding(horizontal = 9.dp, vertical = 5.dp),
                        text = "🦟 주의",
                        color = Color(0xFFD02020),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
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
                /* 펼친 상태에서만 전체 목록 스크롤을 허용 */
                TopRankingCards(rankingItems = topThree)

                if (expanded && lowerRanking.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(0xFFE2E7DE)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "4위 이하",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF747C72)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    lowerRanking.forEachIndexed { index, district ->
                        val rank = district.rank.takeIf { it > 0 } ?: index + 4
                        ExpandedRankingRow(
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
private fun TopRankingCards(
    rankingItems: List<DistrictRankingItem>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rankingItems.forEachIndexed { index, district ->
            TopRankingCard(
                rank = district.rank.takeIf { it > 0 } ?: index + 1,
                district = district,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TopRankingCard(
    rank: Int,
    district: DistrictRankingItem,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val assetPath = rankingImageAssetPath(district.districtName)
    /* 지역별 랭킹 이미지를 assets에서 읽고 없으면 순위별 배경색을 사용 */
    val imageBitmap = assetPath?.let { path ->
        runCatching {
            context.assets.open(path).use { input ->
                BitmapFactory.decodeStream(input).asImageBitmap()
            }
        }.getOrNull()
    }

    Box(
        modifier = modifier
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(18.dp))
            .background(rankingCardFallbackColor(rank))
    ) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "${district.districtName} 랭킹 이미지",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x44000000),
                            Color(0xCC000000)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .padding(12.dp)
                .width(30.dp)
                .height(30.dp)
                .clip(CircleShape)
                .background(rankBadgeColor(rank)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = district.districtName,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = district.mosquitoIndex.toString(),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

                MosquitoActivityChip(level = district.level)
            }
        }
    }
}

@Composable
private fun ExpandedRankingRow(
    rank: Int,
    district: DistrictRankingItem
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
private fun MosquitoActivityChip(level: MosquitoLevel) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(levelActivityColor(level))
            .padding(horizontal = 7.dp, vertical = 4.dp),
        text = levelCardLabel(level),
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
    )
}

private fun rankingImageAssetPath(districtName: String): String? {
    /* 지역 이름과 assets/ranking 이미지 파일명을 매칭 */
    return when (districtName) {
        "강남구" -> "ranking/gangnam.jpg"
        "강동구" -> "ranking/gangdong.jpg"
        "강북구" -> "ranking/gangbuk.jpg"
        "강서구" -> "ranking/gangseo.jpg"
        "관악구" -> "ranking/gwanak.jpg"
        "광진구" -> "ranking/gwangjin.jpg"
        "구로구" -> "ranking/guro.jpg"
        "금천구" -> "ranking/geumcheon.jpg"
        "노원구" -> "ranking/nowon.jpg"
        "도봉구" -> "ranking/dobong.jpg"
        "동대문구" -> "ranking/dongdaemun.jpg"
        "동작구" -> "ranking/dongjak.jpg"
        "마포구" -> "ranking/mapo.jpg"
        "서대문구" -> "ranking/seodaemun.jpg"
        "서초구" -> "ranking/seocho.jpg"
        "성동구" -> "ranking/seongdong.jpg"
        "성북구" -> "ranking/seongbuk.jpg"
        "송파구" -> "ranking/songpa.jpg"
        "양천구" -> "ranking/yangcheon.jpg"
        "영등포구" -> "ranking/yeongdeungpo.jpg"
        "용산구" -> "ranking/yongsan.jpg"
        "은평구" -> "ranking/eunpyeong.jpg"
        "종로구" -> "ranking/jongno.jpg"
        "중구" -> "ranking/jung.jpg"
        "중랑구" -> "ranking/jungnang.jpg"
        else -> null
    }
}

private fun rankingCardFallbackColor(rank: Int): Color {
    return when (rank) {
        1 -> Color(0xFF4F7EA9)
        2 -> Color(0xFF5F5D6F)
        3 -> Color(0xFFC58A3B)
        else -> Color(0xFF2F7047)
    }
}

private fun rankBadgeColor(rank: Int): Color {
    return when (rank) {
        1 -> Color(0xFFE3BE2C)
        2 -> Color(0xFFA5A8AD)
        3 -> Color(0xFFD88622)
        else -> Color(0xFF747C72)
    }
}

private fun levelActivityLabel(level: MosquitoLevel): String {
    return when (level) {
        MosquitoLevel.VERY_HIGH -> "심각"
        MosquitoLevel.HIGH -> "활발"
        MosquitoLevel.NORMAL -> "보통"
        MosquitoLevel.LOW -> "낮음"
    }
}

private fun levelCardLabel(level: MosquitoLevel): String {
    return when (level) {
        MosquitoLevel.VERY_HIGH -> "심각"
        MosquitoLevel.HIGH -> "활발"
        MosquitoLevel.NORMAL -> "보통"
        MosquitoLevel.LOW -> "낮음"
    }
}

private fun levelActivityColor(level: MosquitoLevel): Color {
    return when (level) {
        MosquitoLevel.VERY_HIGH -> Color(0xFFD02020)
        MosquitoLevel.HIGH -> Color(0xFFC25A20)
        MosquitoLevel.NORMAL -> Color(0xFFD8A213)
        MosquitoLevel.LOW -> Color(0xFF2F7047)
    }
}

private fun todayText(): String {
    return SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA).format(Date())
}

private fun rankingProgressColor(index: Int): Color {
    /* 모기 지수 구간에 따라 순위 리스트의 숫자 색상을 변경 */
    return when {
        index >= 76 -> Color(0xFFD02020)
        index >= 51 -> Color(0xFFC25A20)
        index >= 26 -> Color(0xFFD8A213)
        else -> Color(0xFF2F7047)
    }
}
