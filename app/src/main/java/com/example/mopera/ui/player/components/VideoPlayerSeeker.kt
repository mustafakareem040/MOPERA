package com.example.mopera.ui.VideoPlayer.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import kotlin.time.Duration

@Composable
fun VideoPlayerSeeker(
    focusRequester: FocusRequester,
    state: VideoPlayerState,
    isPlaying: Boolean,
    onPlayPauseToggle: (Boolean) -> Unit,
    contentProgress: Duration,
    contentDuration: Duration
) {
    val contentProgressString = remember(contentProgress) {
        contentProgress.toComponents { h, m, s, _ ->
            if (h > 0) {
                "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            } else {
                "${m.padStartWith0()}:${s.padStartWith0()}"
            }
        }
    }

    val contentDurationString = remember(contentDuration) {
        contentDuration.toComponents { h, m, s, _ ->
            if (h > 0) {
                "$h:${m.padStartWith0()}:${s.padStartWith0()}"
            } else {
                "${m.padStartWith0()}:${s.padStartWith0()}"
            }
        }
    }

    val progress = remember(contentProgress, contentDuration) {
        (contentProgress.inWholeSeconds.toFloat() / contentDuration.inWholeSeconds.toFloat()).coerceIn(0f, 1f)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        VideoPlayerControlsIcon(
            modifier = Modifier.focusRequester(focusRequester),
            icon = if (!isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
            contentDescription = "",
            onClick = { onPlayPauseToggle(!isPlaying) }
        )
        VideoPlayerControllerText(text = contentProgressString)
        VideoPlayerControllerIndicator(progress = progress, state = state)
        VideoPlayerControllerText(text = contentDurationString)
    }
}

private fun Number.padStartWith0() = this.toString().padStart(2, '0')