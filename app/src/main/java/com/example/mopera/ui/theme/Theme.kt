package com.example.mopera.ui.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

@Composable
fun MOPERATheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = darkColorScheme(
        background = DARK80,
        onBackground = White100,
        primary = BLUE40,
        primaryContainer = Black80,
        onPrimaryContainer = White100,
        onPrimary = White100,
        secondaryContainer = Black70,
        onSecondaryContainer = White90,
        surface = Black90,
        surfaceVariant = Black70,
        onSurface = White100,
        onSurfaceVariant = White90,
        secondary = Blue50,
        borderVariant = Gray70,
        border = Blue50,
        errorContainer = Black90,
        onErrorContainer = RED30
    )
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = shapes,
        typography = Typography,
        content = content
    )
}