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
        context.authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[USER_ID] = userId
        }
    }

    suspend fun hasAccessToken(): Boolean {
        return tokens.first().accessToken.isNotBlank()
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
        val USER_ID = stringPreferencesKey("userId")
    }
}
