package com.example.mopera.data

import androidx.compose.runtime.Immutable

@Immutable
data class Media(
    val details: MediaDetails,
    val formats: List<VideoFormat>
)