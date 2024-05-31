package com.example.mopera.data

import kotlinx.serialization.Serializable

@Serializable
data class Episode(
    val nb: Int,
    val season: Int,
    val episodeNummer: Short,
)



