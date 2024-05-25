package com.example.mopera.data
import kotlinx.serialization.SerialInfo
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
    val start: List<String>,
    val end: List<String>
)

@Serializable
data class IntroSkipping(
    val start: String,
    val end: String,
    val control_level: String
)

@Serializable
data class Category(
    @SerialName("en_title") val enTitle: String,
    @SerialName("ar_title") val arTitle: String
)

@Serializable
data class VideoLanguages(
    @SerialName("en_title") val enTitle: String,
    @SerialName("ar_title") val arTitle: String
)


@Serializable
data class MovieDetails(
    val nb: String,
    @SerialName("en_title") val enTitle: String,
    @SerialName("ar_title") val arTitle: String,
    @SerialName("other_title") val otherTitle: String,
    val stars: String,
    val enTranslationFile: String,
    val arTranslationFile: String,
    val fileFile: String,
    @SerialName("ar_content") val arContent: String,
    @SerialName("en_content") val enContent: String,
    val mDate: String,
    val year: String,
    val kind: String,
    val season: String,
    val img: String,
    val imgThumb: String,
    val imgMediumThumb: String,
    val imgObjUrl: String,
    val imgMediumThumbObjUrl: String,
    val filmRating: String,
    val seriesRating: String,
    val episodeNummer: String,
    val rate: String,
    val isSpecial: String,
    val itemDate: String,
    val duration: String,
    val imdbUrlRef: String,
    val rootSeries: String,
    val useParentImg: String,
    val spTranslationFile: String,
    val showComments: Boolean,
    val episode_flag: String,
    val trailer: String,
    val audioStreamNumber: String,
    val parent_skipping: String,
    val collectionID: String,
    val isDeleted: String,
    @SerialName("Likes") val likes: String,
    @SerialName("DisLikes") val dislikes: String,
    val CACHE_SHORT: String? = null,
    val imgThumbObjUrl: String,
    val skippingDurations: SkippingDurations? = null,
    val ratingImg: String,
    val arTranslationFilePath: String,
    val enTranslationFilePath: String,
    val translations: List<Translation>? = null,
    val hasIntroSkipping: Boolean,
    val introSkipping: List<IntroSkipping>? = null,
    val categories: List<Category>? = null,
    val videoLikesNumber: String,
    val videoDisLikesNumber: String,
    val videoLanguages: VideoLanguages,
    val videoCommentsNumber: Int,
    val videoViewsNumber: String,
    val castable: String
)
