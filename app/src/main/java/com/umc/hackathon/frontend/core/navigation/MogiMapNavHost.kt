package com.umc.hackathon.frontend.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.umc.hackathon.frontend.PendingWritePostDestination
import com.umc.hackathon.frontend.feature.home.HomeRoute
import com.umc.hackathon.frontend.feature.mypage.MyPageRoute
import com.umc.hackathon.frontend.feature.onboarding.OnboardingRoute
import com.umc.hackathon.frontend.feature.community.WritePostRoute

@Composable
fun MogiMapNavHost(
    innerPadding: PaddingValues,
    startDestination: String = AppRoute.Onboarding.path,
    shouldNavigateHome: Boolean = false,
    pendingWritePostDestination: PendingWritePostDestination? = null,
    onHomeNavigationHandled: () -> Unit = {},
    onPendingWritePostHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val modifier = Modifier.padding(innerPadding)

    LaunchedEffect(shouldNavigateHome, pendingWritePostDestination) {
        if (shouldNavigateHome) {
            navController.navigate(AppRoute.Home.path) {
                popUpTo(AppRoute.Onboarding.path) {
                    inclusive = true
                }
                launchSingleTop = true
            }

            pendingWritePostDestination?.let { destination ->
                navController.navigate(
                    AppRoute.WritePost.createRoute(
                        districtId = destination.districtId,
                        districtName = destination.districtName
                    )
                ) {
                    launchSingleTop = true
                }
                onPendingWritePostHandled()
            }

            onHomeNavigationHandled()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppRoute.Onboarding.path) {
            OnboardingRoute(
                onFinished = {
                    navController.navigate(AppRoute.Home.path) {
                        popUpTo(AppRoute.Onboarding.path) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoute.Home.path) {
            HomeRoute(
                onNavigateToMyPage = {
                    navController.navigate(AppRoute.MyPage.path)
                },
                onNavigateToWrite = { districtId, districtName ->
                    navController.navigate(
                        AppRoute.WritePost.createRoute(
                            districtId = districtId,
                            districtName = districtName
                        )
                    )
                }
            )
        }

        //로그아웃하면 게스트 지도 홈 화면으로 이동
        composable(AppRoute.MyPage.path) {
            MyPageRoute(
                onBackClick = navController::popBackStack,
                onLoggedOut = {
                    navController.navigate(AppRoute.Home.path) {
                        popUpTo(AppRoute.Home.path) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppRoute.WritePost.path,
            arguments = listOf(
                navArgument(AppRoute.WritePost.DISTRICT_ID) {
                    type = NavType.IntType
                },
                navArgument(AppRoute.WritePost.DISTRICT_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val districtId = backStackEntry.arguments
                ?.getInt(AppRoute.WritePost.DISTRICT_ID)
                ?: 0
            val districtName = backStackEntry.arguments
                ?.getString(AppRoute.WritePost.DISTRICT_NAME)
                .orEmpty()

            WritePostRoute(
                districtId = districtId,
                districtName = districtName,
                onBackClick = navController::popBackStack
            )
        }
    }
}
