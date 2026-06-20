package com.umc.hackathon.frontend.feature.home

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.umc.hackathon.frontend.PendingWritePostNavigation
import com.umc.hackathon.frontend.core.data.AuthTokenStore
import com.umc.hackathon.frontend.core.model.DistrictMosquitoIndex
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import com.umc.hackathon.frontend.feature.community.CommunitySheet
import com.umc.hackathon.frontend.feature.community.model.CommunityPost
import com.umc.hackathon.frontend.feature.district.DistrictInfoSheet
import com.umc.hackathon.frontend.feature.onboarding.data.repository.AuthRepositoryProvider
import com.umc.hackathon.frontend.feature.ranking.RankingBottomSheet
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
    onNavigateToMyPage: () -> Unit,
    onNavigateToWrite: (Int, String) -> Unit,
    onNavigateToEdit: (Long, Int, String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val authTokenStore = remember(context) {
        AuthTokenStore(context.applicationContext)
    }
    val authRepository = remember {
        AuthRepositoryProvider.create()
    }

    LaunchedEffect(Unit) {
        viewModel.updateLoginState(authTokenStore.hasAccessToken())
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshSelectedDistrictPosts()
                coroutineScope.launch {
                    viewModel.updateLoginState(authTokenStore.hasAccessToken())
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HomeScreen(
        uiState = viewModel.uiState,
        onDistrictClick = viewModel::showDistrictSheet,
        onCommunityClick = viewModel::showCommunitySheet,
        onLikeClick = viewModel::togglePostLike,
        onEditClick = { post ->
            val selectedDistrict = viewModel.uiState.selectedDistrictIndex()
            onNavigateToEdit(
                post.id,
                selectedDistrict.id,
                selectedDistrict.districtName
            )
        },
        onDeleteClick = viewModel::deletePost,
        onWriteClick = {
            val selectedDistrict = viewModel.uiState.selectedDistrictIndex()
            if (viewModel.requestWrite()) {
                onNavigateToWrite(
                    selectedDistrict.id,
                    selectedDistrict.districtName
                )
            }
        },
        onMyPageClick = onNavigateToMyPage,
        onDismissSheet = viewModel::dismissSheets,
        onDismissLoginPrompt = viewModel::dismissLoginPrompt,
        onGoogleClick = {
            if (viewModel.uiState.loginPromptPurpose == LoginPromptPurpose.WRITE) {
                val selectedDistrict = viewModel.uiState.selectedDistrictIndex()
                PendingWritePostNavigation.districtId = selectedDistrict.id
                PendingWritePostNavigation.districtName = selectedDistrict.districtName
            }
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
    onLikeClick: (CommunityPost) -> Unit,
    onEditClick: (CommunityPost) -> Unit,
    onDeleteClick: (CommunityPost) -> Unit,
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

        HomeProfileButton(
            isLoggedIn = uiState.isLoggedIn,
            nickname = uiState.nickname,
            profileImageUrl = uiState.profileImageUrl,
            onClick = onMyPageClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
        )

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
                onLikeClick = onLikeClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onWriteClick = onWriteClick,
                onCloseClick = onDismissSheet,
                onCollapseClick = {
                    onDistrictClick(selectedDistrict?.districtName ?: uiState.selectedDistrict ?: "강남구")
                }
            )
        }
    }

    if (uiState.isLoginPromptVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismissLoginPrompt,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = null
        ) {
            LoginPromptSheet(
                purpose = uiState.loginPromptPurpose,
                onDismissClick = onDismissLoginPrompt,
                onGoogleClick = onGoogleClick
            )
        }
    }
}

@Composable
private fun HomeProfileButton(
    isLoggedIn: Boolean,
    nickname: String,
    profileImageUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fallbackText = if (isLoggedIn) {
        nickname.trim().firstOrNull()?.toString() ?: "?"
    } else {
        "?"
    }

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .border(
                width = 3.dp,
                color = Color.White,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoggedIn && !profileImageUrl.isNullOrBlank()) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                model = profileImageUrl,
                contentDescription = "마이페이지",
                contentScale = ContentScale.Crop,
                error = null,
                fallback = null
            )
        } else {
            Text(
                text = fallbackText,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun LoginPromptSheet(
    purpose: LoginPromptPurpose,
    onDismissClick: () -> Unit,
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
            .padding(horizontal = 28.dp, vertical = 26.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "로그인이 필요합니다",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF151A15)
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onDismissClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "×",
                    color = Color(0xFF747C72),
                    fontSize = 30.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = when (purpose) {
                LoginPromptPurpose.WRITE -> "글을 작성하려면 로그인이 필요해요."
                LoginPromptPurpose.LIKE -> "좋아요를 누르려면 로그인이 필요해요."
            },
            color = Color(0xFF747C72),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(36.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFCAD2C8),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onGoogleClick() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = googleIcon,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Google로 계속하기",
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF151A15)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

private fun HomeUiState.selectedDistrictIndex(): DistrictMosquitoIndex {
    return districtIndexes.firstOrNull {
        it.districtName == selectedDistrict
    } ?: districtIndexes.firstOrNull {
        it.districtName == "강남구"
    } ?: DistrictMosquitoIndex(
        districtName = "강남구",
        mosquitoIndex = 72,
        latitude = 37.5172,
        longitude = 127.0473,
        id = 3
    )
}
