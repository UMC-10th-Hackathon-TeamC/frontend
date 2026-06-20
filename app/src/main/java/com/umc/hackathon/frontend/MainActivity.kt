package com.umc.hackathon.frontend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.umc.hackathon.frontend.core.data.AuthTokenStore
import com.umc.hackathon.frontend.core.network.NetworkModule
import com.umc.hackathon.frontend.core.navigation.AppRoute
import com.umc.hackathon.frontend.core.navigation.MogiMapNavHost
import com.umc.hackathon.frontend.ui.theme.UMCHackathonFrontendTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val shouldNavigateHome = mutableStateOf(false)
    private val startDestination = mutableStateOf<String?>(null)
    private lateinit var authTokenStore: AuthTokenStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        authTokenStore = AuthTokenStore(applicationContext)
        NetworkModule.initialize(applicationContext)
        val isOAuthCallback = handleOAuthCallback(intent)
        if (!isOAuthCallback) {
            loadStartDestination()
        }

        setContent {
            UMCHackathonFrontendTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    startDestination.value?.let { destination ->
                        MogiMapNavHost(
                            innerPadding = innerPadding,
                            startDestination = destination,
                            shouldNavigateHome = shouldNavigateHome.value,
                            onHomeNavigationHandled = {
                                shouldNavigateHome.value = false
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOAuthCallback(intent)
    }

    private fun handleOAuthCallback(intent: Intent?): Boolean {
        val uri = intent?.data ?: return false
        if (!uri.isOAuthCallback()) return false

        val accessToken = uri.getQueryParameter("accessToken").orEmpty()
        val refreshToken = uri.getQueryParameter("refreshToken").orEmpty()
        val userId = uri.getQueryParameter("userId").orEmpty()
        if (accessToken.isBlank() || refreshToken.isBlank()) return false

        startDestination.value = AppRoute.Home.path
        shouldNavigateHome.value = true

        lifecycleScope.launch {
            authTokenStore.saveTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
                userId = userId
            )
        }
        return true
    }

    private fun loadStartDestination() {
        startDestination.value = AppRoute.Onboarding.path
    }

    private fun Uri.isOAuthCallback(): Boolean {
        return scheme == OAUTH_CALLBACK_SCHEME &&
            host == OAUTH_CALLBACK_HOST &&
            path == OAUTH_CALLBACK_PATH
    }

    private companion object {
        const val OAUTH_CALLBACK_SCHEME = "mogi"
        const val OAUTH_CALLBACK_HOST = "oauth"
        const val OAUTH_CALLBACK_PATH = "/callback"
    }
}
