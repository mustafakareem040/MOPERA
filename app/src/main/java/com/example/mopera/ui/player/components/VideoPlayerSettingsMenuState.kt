package com.example.mopera.ui.player.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import com.example.mopera.data.VideoFormat

object VideoPlayerSettingsMenuState {
    var showSubtitlesControls by mutableStateOf(false)
    var showQualityControls by mutableStateOf(false)
    var selectedTrack by mutableStateOf<MediaItem.SubtitleConfiguration?>(null)
    var selectedQuality by mutableStateOf<VideoFormat?>(null)
}
