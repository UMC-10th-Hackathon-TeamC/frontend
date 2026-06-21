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
    private val authTokenStore by lazy {
        AuthTokenStore(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NetworkModule.initialize(applicationContext)
        loadStartDestination()
        handleOAuthCallback(intent)

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

    private fun loadStartDestination() {
        startDestination.value = AppRoute.Onboarding.path
    }

    private fun handleOAuthCallback(intent: Intent?) {
        val uri = intent?.data ?: return
        if (!uri.isOAuthCallback()) return

        val accessToken = uri.getQueryParameter("accessToken").orEmpty()
        val refreshToken = uri.getQueryParameter("refreshToken").orEmpty()
        if (accessToken.isBlank() || refreshToken.isBlank()) return

        lifecycleScope.launch {
            authTokenStore.saveTokens(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
            shouldNavigateHome.value = true
        }
    }

    private fun Uri.isOAuthCallback(): Boolean {
        return scheme == "mogi" && host == "oauth" && path == "/callback"
    }
}
