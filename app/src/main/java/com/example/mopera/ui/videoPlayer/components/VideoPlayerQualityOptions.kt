package com.example.mopera.ui.videoPlayer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text
import com.example.mopera.data.VideoFormat

@Composable
fun VideoPlayerQualityOptions(
    modifier: Modifier,
    formats: List<VideoFormat>,
    onFormatSelected: (VideoFormat) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    Column(modifier = modifier) {
        IconButton(
            onClick = { VideoPlayerSettingsMenuState.showQualityControls = false },
            modifier = Modifier.align(Alignment.End).padding(bottom = 10.dp),
            colors = ButtonDefaults.colors(
                containerColor = Color.White,
                contentColor = Color.Black,
                focusedContainerColor = Color.White.copy(0.85f)
            )
        ) {
            Icon(Icons.Default.Close, "Close", tint = Color.Black)
        }
        if (formats.isEmpty()) {
            Text("No available formats", style = VideoPlayerSettingsMenuState.style)
            IconButton(
                onClick = { VideoPlayerSettingsMenuState.showQualityControls = false },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.colors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    focusedContainerColor = Color.White.copy(0.85f)
                )
            ) {
                Icon(Icons.Default.Close, "Close", tint = Color.Black)
            }
        } else {
            Text(
                text = "Video Quality", fontSize = 32.sp, color = Color.White,
                fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp)
            )

            formats.forEachIndexed { index, format ->
                Button(
                    onClick = { onFormatSelected(format) },
                    modifier = if (index == 0) Modifier.focusRequester(focusRequester) else Modifier,
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        focusedContainerColor = Color.White.copy(0.2f)
                    ),
                    shape = ButtonDefaults.shape(focusedShape = RectangleShape)
                ) {
                    Text(
                        text = format.resolution,
                        style = VideoPlayerSettingsMenuState.style
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}