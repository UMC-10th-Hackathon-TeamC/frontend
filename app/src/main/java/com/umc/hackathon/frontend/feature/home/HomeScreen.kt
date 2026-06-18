package com.umc.hackathon.frontend.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.hackathon.frontend.feature.community.CommunitySheet
import com.umc.hackathon.frontend.feature.district.DistrictInfoSheet
import com.umc.hackathon.frontend.feature.ranking.RankingSheet

@Composable
fun HomeRoute(
    onNavigateToMyPage: () -> Unit,
    onNavigateToWrite: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    HomeScreen(
        uiState = viewModel.uiState,
        onRankingClick = viewModel::showRankingSheet,
        onDistrictClick = { viewModel.showDistrictSheet("강남구") },
        onCommunityClick = viewModel::showCommunitySheet,
        onWriteClick = {
            val districtName = viewModel.uiState.selectedDistrict ?: "강남구"
            if (viewModel.requestWrite()) {
                onNavigateToWrite(districtName)
            }
        },
        onMyPageClick = onNavigateToMyPage,
        onDismissSheet = viewModel::dismissSheets
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    onRankingClick: () -> Unit,
    onDistrictClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onWriteClick: () -> Unit,
    onMyPageClick: () -> Unit,
    onDismissSheet: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home(Map)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "지도 SDK와 자치구 오버레이가 들어갈 자리",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(28.dp))
            Button(onClick = onRankingClick) {
                Text(text = "랭킹 바텀시트 보기")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onDistrictClick) {
                Text(text = "강남구 정보 시트 보기")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onCommunityClick) {
                Text(text = "커뮤니티 바텀시트 보기")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onWriteClick) {
                Text(text = "글쓰기 테스트")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onMyPageClick) {
                Text(text = "마이페이지")
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Text(
                text = "?",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (uiState.isRankingSheetVisible) {
        ModalBottomSheet(onDismissRequest = onDismissSheet) {
            RankingSheet(districtIndexes = uiState.districtIndexes)
        }
    }

    if (uiState.isDistrictSheetVisible) {
        ModalBottomSheet(onDismissRequest = onDismissSheet) {
            DistrictInfoSheet(
                selectedDistrict = uiState.districtIndexes.firstOrNull {
                    it.districtName == uiState.selectedDistrict
                },
                recentPosts = uiState.recentPosts,
                onCommunityClick = onCommunityClick
            )
        }
    }

    if (uiState.isCommunitySheetVisible) {
        ModalBottomSheet(onDismissRequest = onDismissSheet) {
            CommunitySheet(
                districtName = uiState.selectedDistrict ?: "강남구",
                posts = uiState.recentPosts,
                onWriteClick = onWriteClick
            )
        }
    }

    if (uiState.isLoginPromptVisible) {
        ModalBottomSheet(onDismissRequest = onDismissSheet) {
            LoginPromptSheet(onDismiss = onDismissSheet)
        }
    }
}

@Composable
private fun LoginPromptSheet(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "로그인이 필요합니다",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "글을 작성하려면 로그인이 필요해요.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDismiss
        ) {
            Text(text = "Google로 계속하기")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
