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
import androidx.media3.common.MediaItem
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.Text


@Composable
fun VideoPlayerSubtitlesOptions(
    modifier: Modifier,
    tracks: List<MediaItem.SubtitleConfiguration>,
    onTrackSelected: (MediaItem.SubtitleConfiguration) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    Column(modifier = modifier) {
        IconButton(
            onClick = { VideoPlayerSettingsMenuState.showSubtitlesControls = false },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.colors(
                containerColor = Color.White,
                contentColor = Color.Black,
                focusedContainerColor = Color.White.copy(0.85f)
            )
        ) {
            Icon(Icons.Default.Close, "Close", tint = Color.Black)
        }
        if (tracks.isEmpty()) {
            Text("No available subtitles", style = VideoPlayerSettingsMenuState.style)
        }
        else {
                Text(text = "Choose subtitles", fontSize = 32.sp, color = Color.White,
                    fontWeight = FontWeight.Bold, modifier=Modifier.padding(bottom = 10.dp))
                tracks.forEachIndexed { index, track ->
                    track.label?.let { label ->
                        Button(
                            onClick = { onTrackSelected(track) },
                            modifier = if (index == 0) Modifier.focusRequester(focusRequester) else Modifier,
                            colors = ButtonDefaults.colors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White,
                                focusedContainerColor = Color.White.copy(0.2f)
                            ),
                            shape = ButtonDefaults.shape(focusedShape = RectangleShape)
                        ) {
                            Text(
                                text = label.replaceFirstChar { it.uppercase() },
                                style = VideoPlayerSettingsMenuState.style
                            )
                        }
                    }
                }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}
