package com.example.mopera.data

import androidx.compose.runtime.Immutable

@Immutable
data class Media(
    val details: MovieDetails,
    val formats: List<VideoFormat>
)