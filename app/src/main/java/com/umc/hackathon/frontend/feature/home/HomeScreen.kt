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
import com.umc.hackathon.frontend.feature.ranking.RankingBottomSheet

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
    // 구 상세/커뮤니티/로그인 시트가 열리지 않았을 때만 랭킹 시트를 보여준다.
    val shouldShowRankingSheet =
        !uiState.isDistrictSheetVisible &&
                !uiState.isCommunitySheetVisible &&
                !uiState.isLoginPromptVisible

    Box(modifier = Modifier.fillMaxSize()) {
        // 전체 화면 네이버 지도.
        HomeMap(
            districts = uiState.districtIndexes,
            selectedDistrict = uiState.selectedDistrict,
            onDistrictClick = onDistrictClick,
            modifier = Modifier.fillMaxSize()
        )

        // 우측 상단 마이페이지 버튼.
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

        // 홈 기본 상태에서 항상 보이는 랭킹 바텀시트.
        if (shouldShowRankingSheet) {
            RankingBottomSheet(
                districtIndexes = uiState.districtIndexes,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    // 지도 마커 클릭 시 열리는 구 상세 바텀시트.
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

    // 구 상세에서 커뮤니티 보기 클릭 시 열리는 커뮤니티 바텀시트.
    if (uiState.isCommunitySheetVisible) {
        ModalBottomSheet(onDismissRequest = onDismissSheet) {
            CommunitySheet(
                districtName = uiState.selectedDistrict ?: "강남구",
                posts = uiState.recentPosts,
                onWriteClick = onWriteClick
            )
        }
    }

    // 비로그인 사용자가 글쓰기 시도 시 열리는 로그인 유도 바텀시트.
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
