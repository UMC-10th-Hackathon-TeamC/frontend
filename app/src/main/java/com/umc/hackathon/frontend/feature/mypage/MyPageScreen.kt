package com.umc.hackathon.frontend.feature.mypage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
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
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        /* 위치 권한 결과에 따라 실제 위치 또는 기본 위치로 마이페이지 정보를 로드 */
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            loadMyPageWithDeviceLocation(
                context = context,
                viewModel = viewModel,
                isLoggedIn = isLoggedIn == true
            )
        } else {
            loadMyPageWithDefaultLocation(
                viewModel = viewModel,
                isLoggedIn = isLoggedIn == true
            )
        }
    }

    LaunchedEffect(Unit) {
        /* 토큰 존재 여부로 로그인 상태를 먼저 확인한 뒤 화면 데이터를 불러옴 */
        val hasToken = authTokenStore.hasAccessToken()
        isLoggedIn = hasToken

        if (hasToken) {
            viewModel.loadProfile()
        }

        if (hasLocationPermission(context)) {
            loadMyPageWithDeviceLocation(
                context = context,
                viewModel = viewModel,
                isLoggedIn = hasToken
            )
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    MyPageScreen(
        uiState = uiState,
        isAuthChecked = isLoggedIn != null,
        isLoggedIn = isLoggedIn == true,
        onBackClick = onBackClick,
        onLoginClick = {
            /* 비로그인 상태에서 프로필 영역을 누르면 Google 로그인 URL을 브라우저로 열기 */
            val loginIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(authRepository.getGoogleLoginUrl())
            )
            context.startActivity(loginIntent)
        },
        onLogoutClick = {
            /* 로그아웃은 서버 요청 후 로컬 토큰까지 삭제 */
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

        /* 토큰 확인이 끝나기 전에는 로그인/비로그인 화면을 확정하지 않음 */
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

        /* 로그인 여부에 따라 프로필 카드와 로그인 유도 카드를 분기 */
        if (isLoggedIn) {
            ProfileCard(uiState = uiState)
        } else {
            GuestProfileCard(onLoginClick = onLoginClick)
        }

        Spacer(modifier = Modifier.height(20.dp))

        MyDistrictCard(uiState = uiState)

        Spacer(modifier = Modifier.height(20.dp))

        SettingCard(uiState = uiState)

        if (isLoggedIn) {
            Spacer(modifier = Modifier.height(24.dp))
            LogoutButton(onLogoutClick = onLogoutClick)
        }

        if (isLoggedIn) uiState.errorMessage?.let { errorMessage ->
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
            ProfileImage(
                profileImageUrl = uiState.profileImageUrl,
                nickname = uiState.nickname
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = uiState.nickname.ifBlank {
                        if (uiState.isLoading) {
                            "프로필 정보 불러오는 중..."
                        } else {
                            "닉네임 없음"
                        }
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.email.ifBlank {
                        if (uiState.isLoading) {
                            ""
                        } else {
                            "이메일 없음"
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun GuestProfileCard(
    onLoginClick: () -> Unit
) {
    MyPageCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLoginClick() }
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GuestProfileIcon()

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = "비로그인 사용자",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "로그인하기 ->",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            }
        }
    }
}

@Composable
private fun GuestProfileIcon(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(78.dp)
            .clip(CircleShape)
            .background(Color(0xFFE8EFE7)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(34.dp)) {
            val stroke = Stroke(width = 4.dp.toPx())
            val iconColor = TextSecondary

            drawCircle(
                color = iconColor,
                radius = 8.dp.toPx(),
                center = Offset(size.width / 2f, 9.dp.toPx()),
                style = stroke
            )
            drawArc(
                color = iconColor,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(4.dp.toPx(), 16.dp.toPx()),
                size = Size(
                    width = 26.dp.toPx(),
                    height = 20.dp.toPx()
                ),
                style = stroke
            )
        }
    }
}

@Composable
private fun ProfileImage(
    profileImageUrl: String?,
    nickname: String
) {
    /* 서버 프로필 이미지가 없으면 닉네임 첫 글자를 대체 이미지로 표시 */
    Box(
        modifier = Modifier
            .size(78.dp)
            .clip(CircleShape)
            .background(PrimaryGreen),
        contentAlignment = Alignment.Center
    ) {
        if (profileImageUrl.isNullOrBlank()) {
            ProfileImageFallback(nickname = nickname)
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
private fun ProfileImageFallback(
    nickname: String
) {
    val initial = nickname.trim().firstOrNull()?.toString() ?: "?"
    Text(
        text = initial,
        color = Color.White,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun MyDistrictCard(
    uiState: MyPageUiState
) {
    /* 현재 위치 또는 기본 위치 기준의 지역 모기 지수를 표시 */
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
                        text = uiState.districtName.ifBlank {
                            if (uiState.isLoading) "위치 확인 중" else "확인 불가"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (uiState.isLoading) "GPS 확인 중..." else uiState.locationStatusText,
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
                description = uiState.districtName.ifBlank {
                    if (uiState.isLoading) "위치 확인 중" else "확인 불가"
                }
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

private fun hasLocationPermission(context: Context): Boolean {
    /* 정확한 위치와 대략적 위치 중 하나라도 허용되면 위치 기반 조회 가능 */
    val hasFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return hasFineLocation || hasCoarseLocation
}

@SuppressLint("MissingPermission")
private fun loadMyPageWithDeviceLocation(
    context: Context,
    viewModel: MyPageViewModel,
    isLoggedIn: Boolean
) {
    /* 권한이 없거나 위치를 못 가져오면 기본 위치 기준으로 대체 */
    if (!hasLocationPermission(context)) {
        loadMyPageWithDefaultLocation(
            viewModel = viewModel,
            isLoggedIn = isLoggedIn
        )
        return
    }

    val locationClient = LocationServices.getFusedLocationProviderClient(context)
    locationClient
        .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location == null) {
                loadMyPageWithDefaultLocation(
                    viewModel = viewModel,
                    isLoggedIn = isLoggedIn
                )
                return@addOnSuccessListener
            }

            /* 로그인 사용자는 프로필과 지역 정보를 함께, 게스트는 지역 정보만 조회 */
            if (isLoggedIn) {
                viewModel.loadMyPage(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            } else {
                viewModel.loadCurrentDistrict(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
        }
        .addOnFailureListener {
            loadMyPageWithDefaultLocation(
                viewModel = viewModel,
                isLoggedIn = isLoggedIn
            )
        }
}

private fun loadMyPageWithDefaultLocation(
    viewModel: MyPageViewModel,
    isLoggedIn: Boolean
) {
    /* 위치 조회 실패 시 강남구 기본 좌표로 마이페이지 데이터를 로드 */
    if (isLoggedIn) {
        viewModel.loadMyPageWithDefaultLocation()
    } else {
        viewModel.loadCurrentDistrictWithDefaultLocation()
    }
}
