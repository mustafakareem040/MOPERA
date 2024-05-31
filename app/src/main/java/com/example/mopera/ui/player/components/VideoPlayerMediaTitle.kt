package com.example.mopera.ui.VideoPlayer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text


@Stable
@Composable
fun VideoPlayerMediaTitle(
    title: String,
    secondaryText: String,
    tertiaryText: String,
    modifier: Modifier = Modifier
) {
    val subTitle = buildString {
        append(secondaryText)
        if (secondaryText.isNotEmpty() && tertiaryText.isNotEmpty()) append(" • ")
        append(tertiaryText)
    }
    Column(modifier.fillMaxWidth()) {
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subTitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}