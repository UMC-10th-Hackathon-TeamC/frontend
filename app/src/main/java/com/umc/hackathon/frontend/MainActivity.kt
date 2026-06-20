package com.umc.hackathon.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.umc.hackathon.frontend.core.network.NetworkModule
import com.umc.hackathon.frontend.core.navigation.AppRoute
import com.umc.hackathon.frontend.core.navigation.MogiMapNavHost
import com.umc.hackathon.frontend.ui.theme.UMCHackathonFrontendTheme

class MainActivity : ComponentActivity() {
    private val shouldNavigateHome = mutableStateOf(false)
    private val startDestination = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NetworkModule.initialize(applicationContext)
        loadStartDestination()

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

    private fun loadStartDestination() {
        startDestination.value = AppRoute.Onboarding.path
    }
}
