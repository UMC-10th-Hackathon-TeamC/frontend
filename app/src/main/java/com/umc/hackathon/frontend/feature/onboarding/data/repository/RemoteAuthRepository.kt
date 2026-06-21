package com.umc.hackathon.frontend.feature.onboarding.data.repository

import com.umc.hackathon.frontend.BuildConfig

class RemoteAuthRepository : AuthRepository {
    override fun getGoogleLoginUrl(): String {
        /* Google 로그인은 Retrofit 호출이 아니라 브라우저로 열 URL을 만들어 시작 */
        return "${BuildConfig.API_BASE_URL}oauth2/login/google"
    }

    override suspend fun continueAsGuest() = Unit
}
