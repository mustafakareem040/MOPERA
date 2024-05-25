package com.example.mopera.ui.videoPlayer.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

object VideoPlayerSettingsMenuState {
    var showSubtitlesControls by mutableStateOf(false)
    var showQualityControls by mutableStateOf(false)
    val style = TextStyle(color = Color.White, fontSize = 24.sp,
        shadow = Shadow(color = Color.Black, offset = Offset(0f, 1f), blurRadius = 5f)
    )
}
