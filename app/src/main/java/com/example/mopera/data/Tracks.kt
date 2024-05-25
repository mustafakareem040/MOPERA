package com.example.mopera.data

import kotlinx.serialization.Serializable

@Serializable
data class Tracks(
    val arTranslationFile: String,
    val enTranslationFile: String,
    val spTranslationFile: String,
    val arTranslationFilePath: String,
    val enTranslationFilePath: String,
    val translations: List<Track>
)

@Serializable
data class Track(
    val id: Int,
    val name: String,
    val type: String,
    val extention: String,
    val file: String
)

