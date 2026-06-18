package com.umc.hackathon.frontend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import com.umc.hackathon.frontend.core.navigation.AppRoute
import com.umc.hackathon.frontend.core.navigation.MogiMapNavHost
import com.umc.hackathon.frontend.ui.theme.UMCHackathonFrontendTheme

class MainActivity : ComponentActivity() {
    private val oauthCallbackVersion = mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleOAuthCallback(intent)

        setContent {
            UMCHackathonFrontendTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MogiMapNavHost(
                        innerPadding = innerPadding,
                        startDestination = if (hasAccessToken()) {
                            AppRoute.Home.path
                        } else {
                            AppRoute.Onboarding.path
                        },
                        oauthCallbackVersion = oauthCallbackVersion.intValue
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOAuthCallback(intent)
    }

    private fun handleOAuthCallback(intent: Intent?) {
        val uri = intent?.data ?: return
        if (!uri.isOAuthCallback()) return

        val accessToken = uri.getQueryParameter("accessToken").orEmpty()
        val refreshToken = uri.getQueryParameter("refreshToken").orEmpty()
        if (accessToken.isBlank() || refreshToken.isBlank()) return

        getSharedPreferences(AUTH_PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()

        oauthCallbackVersion.intValue += 1
    }

    private fun hasAccessToken(): Boolean {
        return !getSharedPreferences(AUTH_PREFS_NAME, MODE_PRIVATE)
            .getString(KEY_ACCESS_TOKEN, null)
            .isNullOrBlank()
    }

    private fun Uri.isOAuthCallback(): Boolean {
        return scheme == OAUTH_CALLBACK_SCHEME &&
            host == OAUTH_CALLBACK_HOST &&
            path == OAUTH_CALLBACK_PATH
    }

    private companion object {
        const val AUTH_PREFS_NAME = "auth"
        const val KEY_ACCESS_TOKEN = "accessToken"
        const val KEY_REFRESH_TOKEN = "refreshToken"
        const val OAUTH_CALLBACK_SCHEME = "mogi"
        const val OAUTH_CALLBACK_HOST = "oauth"
        const val OAUTH_CALLBACK_PATH = "/callback"
    }
}
