package com.umc.hackathon.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.umc.hackathon.frontend.core.navigation.MogiMapNavHost
import com.umc.hackathon.frontend.ui.theme.UMCHackathonFrontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UMCHackathonFrontendTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MogiMapNavHost(innerPadding = innerPadding)
                }
            }
        }
    }
}
