package com.example.mopera.data

import kotlinx.serialization.Serializable

@Serializable
data class SkippingDuration(
    val start: Float,
    val end: Float
)
