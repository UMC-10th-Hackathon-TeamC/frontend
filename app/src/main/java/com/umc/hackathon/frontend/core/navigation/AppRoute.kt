package com.umc.hackathon.frontend.core.navigation

import android.net.Uri

sealed class AppRoute(val path: String) {
    data object Onboarding : AppRoute("onboarding")
    data object Home : AppRoute("home")
    data object MyPage : AppRoute("mypage")

    data object WritePost : AppRoute("write/{districtName}") {
        const val DISTRICT_NAME = "districtName"

        fun createRoute(districtName: String): String {
            return "write/${Uri.encode(districtName)}"
        }
    }
}
