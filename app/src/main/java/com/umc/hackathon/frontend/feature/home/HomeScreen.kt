package com.umc.hackathon.frontend.feature.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.umc.hackathon.frontend.PendingWritePostNavigation
import com.umc.hackathon.frontend.core.data.AuthTokenStore
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import com.umc.hackathon.frontend.feature.community.CommunitySheet
import com.umc.hackathon.frontend.feature.district.DistrictInfoSheet
import com.umc.hackathon.frontend.feature.onboarding.data.repository.AuthRepositoryProvider
import com.umc.hackathon.frontend.feature.ranking.RankingBottomSheet

@Composable
fun HomeRoute(
    onNavigateToMyPage: () -> Unit,
    onNavigateToWrite: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val authTokenStore = remember(context) {
        AuthTokenStore(context.applicationContext)
    }
    val authRepository = remember {
        AuthRepositoryProvider.create()
    }

    LaunchedEffect(Unit) {
        viewModel.updateLoginState(authTokenStore.hasAccessToken())
    }

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
        onDismissSheet = viewModel::dismissSheets,
        onDismissLoginPrompt = viewModel::dismissLoginPrompt,
        onGoogleClick = {
            PendingWritePostNavigation.districtName = viewModel.uiState.selectedDistrict ?: "강남구"
            val loginIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(authRepository.getGoogleLoginUrl())
            )
            context.startActivity(loginIntent)
        }
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
    onDismissSheet: () -> Unit,
    onDismissLoginPrompt: () -> Unit,
    onGoogleClick: () -> Unit
) {
    val communitySheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val shouldShowRankingSheet =
        !uiState.isDistrictSheetVisible &&
            !uiState.isCommunitySheetVisible &&
            !uiState.isLoginPromptVisible

    Box(modifier = Modifier.fillMaxSize()) {
        HomeMap(
            districts = uiState.districtIndexes,
            selectedDistrict = uiState.selectedDistrict,
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

        if (shouldShowRankingSheet) {
            RankingBottomSheet(
                districtIndexes = uiState.districtIndexes,
                districtRanking = uiState.districtRanking,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
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

    if (uiState.isCommunitySheetVisible && !uiState.isLoginPromptVisible) {
        val selectedDistrict = uiState.districtIndexes.firstOrNull {
            it.districtName == uiState.selectedDistrict
        }

        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            containerColor = MaterialTheme.colorScheme.surface,
            sheetState = communitySheetState
        ) {
            CommunitySheet(
                districtName = selectedDistrict?.districtName ?: uiState.selectedDistrict ?: "강남구",
                mosquitoIndex = selectedDistrict?.mosquitoIndex ?: 72,
                level = selectedDistrict?.level ?: MosquitoLevel.HIGH,
                posts = uiState.recentPosts,
                onWriteClick = onWriteClick,
                onCloseClick = onDismissSheet,
                onCollapseClick = {
                    onDistrictClick(selectedDistrict?.districtName ?: uiState.selectedDistrict ?: "강남구")
                }
            )
        }
    }

    if (uiState.isLoginPromptVisible) {
        ModalBottomSheet(onDismissRequest = onDismissLoginPrompt) {
            LoginPromptSheet(onGoogleClick = onGoogleClick)
        }
    }
}

@Composable
private fun LoginPromptSheet(
    onGoogleClick: () -> Unit
) {
    val context = LocalContext.current
    val googleIcon = remember(context) {
        context.assets.open("login/logo_google.png").use { input ->
            BitmapFactory.decodeStream(input).asImageBitmap()
        }
    }

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
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            onClick = onGoogleClick,
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val iconSize = 110.dp
                val iconStart = maxWidth / 4 - iconSize / 2

                Image(
                    bitmap = googleIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = iconStart)
                        .size(iconSize)
                )
                Text(
                    text = "Google로 계속하기",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
