package com.example.mopera.ui.VideoPlayer.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp

@Composable
fun VideoPlayerOverlay(
    modifier: Modifier = Modifier,
    state: VideoPlayerState = rememberVideoPlayerState(),
    focusRequester: FocusRequester = remember { FocusRequester() },
    centerButton: @Composable () -> Unit = {},
    subtitles: @Composable () -> Unit = {},
    controls: @Composable () -> Unit = {}
) {
    val controlsVisible by state.controlsVisible.collectAsState()

    LaunchedEffect(controlsVisible) {
        if (controlsVisible) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(controlsVisible, Modifier, fadeIn(), fadeOut()) {
            CinematicBackground(Modifier.fillMaxSize())
        }

        Column {
            Box(
                Modifier.weight(1f),
                contentAlignment = Alignment.TopEnd
            ) {
                subtitles()
            }
            AnimatedVisibility(
                controlsVisible,
                Modifier,
                slideInVertically { it },
                slideOutVertically { it }
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 56.dp)
                        .padding(bottom = 32.dp, top = 8.dp)
                ) {
                    controls()
                }
            }
        }
        centerButton()
    }
}

@Composable
fun CinematicBackground(modifier: Modifier = Modifier) {
    Spacer(
        modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color.Black.copy(alpha = 0.1f),
                    Color.Black.copy(alpha = 0.8f)
                )
            )
        )
    )
}