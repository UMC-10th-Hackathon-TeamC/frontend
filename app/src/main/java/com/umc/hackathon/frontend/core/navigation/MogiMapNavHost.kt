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
import com.umc.hackathon.frontend.feature.home.HomeRoute
import com.umc.hackathon.frontend.feature.mypage.MyPageRoute
import com.umc.hackathon.frontend.feature.onboarding.OnboardingRoute
import com.umc.hackathon.frontend.feature.community.WritePostRoute

@Composable
fun MogiMapNavHost(
    innerPadding: PaddingValues,
    startDestination: String = AppRoute.Onboarding.path,
    shouldNavigateHome: Boolean = false,
    onHomeNavigationHandled: () -> Unit = {}
) {
    val navController = rememberNavController()
    val modifier = Modifier.padding(innerPadding)

    LaunchedEffect(shouldNavigateHome) {
        if (shouldNavigateHome) {
            navController.navigate(AppRoute.Home.path) {
                popUpTo(AppRoute.Onboarding.path) {
                    inclusive = true
                }
                launchSingleTop = true
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
                onNavigateToWrite = { districtName ->
                    navController.navigate(AppRoute.WritePost.createRoute(districtName))
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
                navArgument(AppRoute.WritePost.DISTRICT_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val districtName = backStackEntry.arguments
                ?.getString(AppRoute.WritePost.DISTRICT_NAME)
                .orEmpty()

            WritePostRoute(
                districtName = districtName,
                onBackClick = navController::popBackStack
            )
        }
    }
}
