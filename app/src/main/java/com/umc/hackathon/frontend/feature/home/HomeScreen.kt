package com.umc.hackathon.frontend.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
        onDistrictClick = viewModel::showDistrictSheet,
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
    onDistrictClick: (String) -> Unit,
    onCommunityClick: () -> Unit,
    onWriteClick: () -> Unit,
    onMyPageClick: () -> Unit,
    onDismissSheet: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeMap(
            districts = uiState.districtIndexes,
            onDistrictClick = onDistrictClick,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable { onMyPageClick() }
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
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            DistrictInfoSheet(
                selectedDistrict = uiState.districtIndexes.firstOrNull {
                    it.districtName == uiState.selectedDistrict
                },
                recentPosts = uiState.recentPosts,
                onCommunityClick = onCommunityClick,
                onCloseClick = onDismissSheet
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