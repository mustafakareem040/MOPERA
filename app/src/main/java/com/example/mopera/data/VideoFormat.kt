package com.example.mopera.data

import kotlinx.serialization.Serializable

@Serializable
data class VideoFormat(
    val name: String,
    val resolution: String,
    val container: String,
    val videoUrl: String
)

