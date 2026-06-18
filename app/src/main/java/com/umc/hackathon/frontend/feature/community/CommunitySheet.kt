package com.umc.hackathon.frontend.feature.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.umc.hackathon.frontend.feature.community.model.CommunityPost

@Composable
fun CommunitySheet(
    districtName: String,
    posts: List<CommunityPost>,
    onWriteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "$districtName 커뮤니티",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(text = "전체  제보  질문  팁  잡담")
        posts.forEach { post ->
            Text(text = "${post.authorName}  ${post.category}")
            Text(text = post.content)
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onWriteClick
        ) {
            Text(text = "$districtName 커뮤니티에 글 쓰기")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
