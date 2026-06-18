package com.umc.hackathon.frontend.feature.district

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.feature.community.model.CommunityPost

@Composable
fun DistrictInfoSheet(
    selectedDistrict: DistrictMosquitoIndex?,
    recentPosts: List<CommunityPost>,
    onCommunityClick: () -> Unit
) {
    val districtName = selectedDistrict?.districtName ?: "강남구"
    val mosquitoIndex = selectedDistrict?.mosquitoIndex ?: 72
    val levelLabel = selectedDistrict?.level?.label ?: "높음"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = districtName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = levelLabel,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        Text(text = "모기 지수 $mosquitoIndex / 100")
        Text(
            text = "최근 게시글",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        recentPosts.take(2).forEach { post ->
            Text(text = "${post.category}  ${post.title}")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCommunityClick
        ) {
            Text(text = "커뮤니티 보기")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
