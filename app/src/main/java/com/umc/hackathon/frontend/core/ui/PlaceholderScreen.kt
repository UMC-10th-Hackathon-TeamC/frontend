package com.umc.hackathon.frontend.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaceholderScreen(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    primaryButtonText: String? = null,
    onPrimaryClick: (() -> Unit)? = null,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(28.dp))

        if (primaryButtonText != null && onPrimaryClick != null) {
            Button(onClick = onPrimaryClick) {
                Text(text = primaryButtonText)
            }
        }

        if (secondaryButtonText != null && onSecondaryClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onSecondaryClick) {
                Text(text = secondaryButtonText)
            }
        }
    }
}
