package com.umc.hackathon.frontend.core.navigation

import android.net.Uri

sealed class AppRoute(val path: String) {
    data object Onboarding : AppRoute("onboarding")
    data object Home : AppRoute("home")
    data object MyPage : AppRoute("mypage")

    data object WritePost : AppRoute("write/{districtId}/{districtName}") {
        const val DISTRICT_ID = "districtId"
        const val DISTRICT_NAME = "districtName"

        fun createRoute(
            districtId: Int,
            districtName: String
        ): String {
            return "write/$districtId/${Uri.encode(districtName)}"
        }
    }
}
