package com.example.mopera.ui.videoPlayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Forward10
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.milliseconds

object VideoPlayerPulse {
    enum class Type { FORWARD, BACK, NONE }
}

@Composable
fun VideoPlayerPulse(
    state: VideoPlayerPulseState = rememberVideoPlayerPulseState()
) {
    val icon = when (state.type) {
        VideoPlayerPulse.Type.FORWARD -> Icons.Rounded.Forward10
        VideoPlayerPulse.Type.BACK -> Icons.Rounded.Replay
        VideoPlayerPulse.Type.NONE -> null
    }
    if (icon != null) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier
                .background(Color.White, CircleShape)
                .size(88.dp)
                .wrapContentSize()
                .size(48.dp)
        )
    }
}

class VideoPlayerPulseState {
    private var _type by mutableStateOf(VideoPlayerPulse.Type.NONE)
    val type: VideoPlayerPulse.Type get() = _type

    private val channel = Channel<Unit>(Channel.CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow()
            .debounce(500.milliseconds)
            .collect { _type = VideoPlayerPulse.Type.NONE }
    }

    fun setType(type: VideoPlayerPulse.Type) {
        _type = type
        channel.trySend(Unit)
    }
}

@Composable
fun rememberVideoPlayerPulseState() =
    remember { VideoPlayerPulseState() }.also { LaunchedEffect(it) { it.observe() } }
