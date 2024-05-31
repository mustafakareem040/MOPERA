package com.example.mopera.data
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Translation(
    val id: Int,
    val name: String,
    val type: String,
    val extention: String,
    val file: String
)

@Serializable
data class SkippingDurations(
    val start: List<Double>,
    val end: List<Double>
)

@Serializable
data class IntroSkipping(
    val start: Double,
    val end: Double,
)

@Serializable
data class Category(
    @SerialName("en_title") val enTitle: String,
    @SerialName("ar_title") val arTitle: String
)



@Serializable
data class MovieDetails(
    val nb: Int,
    @SerialName("en_title") val enTitle: String,
    @SerialName("ar_title") val arTitle: String,
    @SerialName("other_title") val otherTitle: String,
    val stars: String,
    val year: String,
    val kind: String,
    val season: String,
    val imgMediumThumbObjUrl: String,
    val episodeNummer: String,
    val rate: String,
    val isSpecial: String,
    val duration: String,
    val parent_skipping: String,
    val isDeleted: String,
    @SerialName("Likes") val likes: String,
    @SerialName("DisLikes") val dislikes: String,
    val imgThumbObjUrl: String,
    val skippingDurations: SkippingDurations? = null,
    val translations: List<Translation>? = null,
    val hasIntroSkipping: Boolean,
    val introSkipping: List<IntroSkipping>? = null,
    val categories: List<Category>? = null,
)
