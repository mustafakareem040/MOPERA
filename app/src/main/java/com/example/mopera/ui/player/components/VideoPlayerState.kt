package com.example.mopera.ui.VideoPlayer.components

import androidx.annotation.IntRange
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class VideoPlayerState internal constructor(
    @IntRange(from = 0)
    private val hideSeconds: Int
) {
    private val _controlsVisible = MutableStateFlow(true)
    val controlsVisible: StateFlow<Boolean> get() = _controlsVisible.asStateFlow()

    private val hideControlsFlow = MutableSharedFlow<Int>(replay = 1, extraBufferCapacity = 1)

    fun showControls(seconds: Int = hideSeconds) {
        _controlsVisible.value = true
        hideControlsFlow.tryEmit(seconds)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun hideControls() {
        hideControlsFlow.resetReplayCache()
        _controlsVisible.value = false
    }

    @OptIn(FlowPreview::class)
    fun observe(scope: CoroutineScope) {
        scope.launch {
            hideControlsFlow
                .debounce { it.toLong() * 1000 }
                .collect {
                    _controlsVisible.value = false
                }
        }
    }
}

@Composable
fun rememberVideoPlayerState(@IntRange(from = 0) hideSeconds: Int = 2): VideoPlayerState {
    val videoPlayerState = remember { VideoPlayerState(hideSeconds) }
    LaunchedEffect(videoPlayerState) {
        videoPlayerState.observe(this)
    }
    return videoPlayerState
}