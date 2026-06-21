package com.umc.hackathon.frontend.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth")

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)

class AuthTokenStore(
    private val context: Context
) {
    /* DataStore에 저장된 토큰 값을 앱에서 쓰는 AuthTokens 형태로 변환 */
    val tokens = context.authDataStore.data.map { preferences ->
        AuthTokens(
            accessToken = preferences[ACCESS_TOKEN].orEmpty(),
            refreshToken = preferences[REFRESH_TOKEN].orEmpty(),
            userId = preferences[USER_ID].orEmpty()
        )
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        userId: String = ""
    ) {
        /* 로그인 성공 또는 토큰 재발급 시 인증에 필요한 값을 저장 */
        context.authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[USER_ID] = userId
        }
    }

    suspend fun hasAccessToken(): Boolean {
        /* accessToken 존재 여부로 현재 로그인 상태를 판단 */
        return tokens.first().accessToken.isNotBlank()
    }

    suspend fun clearTokens() {
        /* 로그아웃 시 저장된 인증 정보를 모두 제거 */
        context.authDataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
            preferences.remove(USER_ID)
        }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
        val USER_ID = stringPreferencesKey("userId")
    }
}
