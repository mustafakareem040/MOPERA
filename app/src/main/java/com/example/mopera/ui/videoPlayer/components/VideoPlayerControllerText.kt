package com.example.mopera.ui.videoPlayer.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text

@Composable
fun VideoPlayerControllerText(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 12.dp),
        text = text,
        color = Color.White,
        fontWeight = FontWeight.SemiBold
    )
}
