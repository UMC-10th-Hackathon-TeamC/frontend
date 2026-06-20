package com.umc.hackathon.frontend.feature.mypage

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.umc.hackathon.frontend.core.data.AuthTokenStore
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import com.umc.hackathon.frontend.feature.onboarding.data.repository.AuthRepositoryProvider

private val MyPageBackground = Color(0xFFF3FAF1)
private val PrimaryGreen = Color(0xFF2F7047)
private val TextPrimary = Color(0xFF151A15)
private val TextSecondary = Color(0xFF747C72)
private val DividerColor = Color(0xFFE2E7DE)

@Composable
fun MyPageRoute(
    onBackClick: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: MyPageViewModel = viewModel()
) {
    val context = LocalContext.current
    val authTokenStore = remember(context) {
        AuthTokenStore(context.applicationContext)
    }
    val authRepository = remember {
        AuthRepositoryProvider.create()
    }
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        val hasToken = authTokenStore.hasAccessToken()
        isLoggedIn = hasToken
        if (hasToken) {
            viewModel.loadMyPage()
        }
    }

    MyPageScreen(
        uiState = uiState,
        isAuthChecked = isLoggedIn != null,
        isLoggedIn = isLoggedIn == true,
        onBackClick = onBackClick,
        onLoginClick = {
            val loginIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(authRepository.getGoogleLoginUrl())
            )
            context.startActivity(loginIntent)
        },
        onLogoutClick = {
            viewModel.logout(
                authTokenStore = authTokenStore,
                onLoggedOut = onLoggedOut
            )
        }
    )
}

@Composable
private fun MyPageScreen(
    uiState: MyPageUiState,
    isAuthChecked: Boolean,
    isLoggedIn: Boolean,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPageBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 34.dp)
    ) {
        MyPageTopBar(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(24.dp))

        if (!isAuthChecked) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "로그인 상태를 확인 중...",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            return@Column
        }

        if (!isLoggedIn) {
            LoginRequiredCard(onLoginClick = onLoginClick)
            return@Column
        }

        ProfileCard(uiState = uiState)

        Spacer(modifier = Modifier.height(20.dp))

        MyDistrictCard(uiState = uiState)

        Spacer(modifier = Modifier.height(20.dp))

        SettingCard(uiState = uiState)

        Spacer(modifier = Modifier.height(24.dp))

        LogoutButton(onLogoutClick = onLogoutClick)

        uiState.errorMessage?.let { errorMessage ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = errorMessage,
                color = Color(0xFFD02020),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MyPageTopBar(
    onBackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8EFE7))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "<",
                color = Color(0xFF404A40),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "마이페이지",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun ProfileCard(
    uiState: MyPageUiState
) {
    MyPageCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage(profileImageUrl = uiState.profileImageUrl)

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = uiState.nickname.ifBlank { "모기맵유저" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.email.ifBlank { "user@gmail.com" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun LoginRequiredCard(
    onLoginClick: () -> Unit
) {
    MyPageCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "모",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "로그인이 필요합니다",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "내 지역 모기 지수와 프로필 정보를 확인하려면 로그인해주세요.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .clickable { onLoginClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Google로 계속하기",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ProfileImage(
    profileImageUrl: String?
) {
    Box(
        modifier = Modifier
            .size(78.dp)
            .clip(CircleShape)
            .background(PrimaryGreen),
        contentAlignment = Alignment.Center
    ) {
        if (profileImageUrl.isNullOrBlank()) {
            ProfileImageFallback()
        } else {
            AsyncImage(
                modifier = Modifier
                    .size(78.dp)
                    .clip(CircleShape),
                model = profileImageUrl,
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                error = null,
                fallback = null
            )
        }
    }
}

@Composable
private fun ProfileImageFallback() {
    Text(
        text = "모",
        color = Color.White,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun MyDistrictCard(
    uiState: MyPageUiState
) {
    MyPageCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "내 지역 모기 지수",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF404A40)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = uiState.districtName.ifBlank { "강남구" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (uiState.isLoading) "GPS 확인 중..." else "GPS 확인 완료",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }

                MosquitoLevelChip(level = uiState.mosquitoLevel)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = uiState.mosquitoIndex.toString(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = mosquitoLevelTextColor(uiState.mosquitoLevel)
                )
            }
        }
    }
}

@Composable
private fun SettingCard(
    uiState: MyPageUiState
) {
    MyPageCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            SettingRow(
                title = "알림 설정",
                description = "모기 지수 알림을 설정하세요"
            )

            HorizontalDivider(color = DividerColor)

            SettingRow(
                title = "지역 변경",
                description = uiState.districtName.ifBlank { "강남구" }
            )

            HorizontalDivider(color = DividerColor)

            SettingRow(
                title = "앱 정보",
                description = "v1.0.0"
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Text(
            text = ">",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFB7C0B5)
        )
    }
}

@Composable
private fun LogoutButton(
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFFD6D2))
            .clickable { onLogoutClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "로그아웃",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD02020)
        )
    }
}

@Composable
private fun MosquitoLevelChip(
    level: MosquitoLevel
) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(mosquitoLevelBackgroundColor(level))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        text = level.label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = mosquitoLevelTextColor(level)
    )
}

@Composable
private fun MyPageCard(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        content = content
    )
}

private fun mosquitoLevelBackgroundColor(level: MosquitoLevel): Color {
    return when (level) {
        MosquitoLevel.VERY_HIGH -> Color(0xFFFFD9D6)
        MosquitoLevel.HIGH -> Color(0xFFFFDEC2)
        MosquitoLevel.NORMAL -> Color(0xFFFFF0B8)
        MosquitoLevel.LOW -> Color(0xFFDDEEE3)
    }
}

private fun mosquitoLevelTextColor(level: MosquitoLevel): Color {
    return when (level) {
        MosquitoLevel.VERY_HIGH -> Color(0xFFD02020)
        MosquitoLevel.HIGH -> Color(0xFFC25A20)
        MosquitoLevel.NORMAL -> Color(0xFFD8A213)
        MosquitoLevel.LOW -> PrimaryGreen
    }
}
