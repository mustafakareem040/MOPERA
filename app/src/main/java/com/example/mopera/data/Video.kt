package com.example.mopera.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A title from Cinemana's list endpoints (latest movies / latest series).
 *
 * Everything except [nb] is optional, so one odd item can't break a whole row.
 */
@Immutable
@Serializable
data class Video(
    val nb: Int,
    @SerialName("en_title") val enTitle: String? = null,
    @SerialName("ar_title") val arTitle: String? = null,
    val stars: String? = null,
    val year: String? = null,
    val kind: String? = null,
    val season: String? = null,
    val episodeNummer: String? = null,
    val imgObjUrl: String? = null,
    val imgMediumThumbObjUrl: String? = null,
) {
    val isSeries: Boolean get() = kind?.trim() == "2"

    val title: String get() = enTitle?.takeIf { it.isNotBlank() } ?: arTitle.orEmpty()

    val posterUrl: String? get() = imgMediumThumbObjUrl.orNullIfBlank() ?: imgObjUrl.orNullIfBlank()

    val backdropUrl: String? get() = imgObjUrl.orNullIfBlank() ?: imgMediumThumbObjUrl.orNullIfBlank()
}

private fun String?.orNullIfBlank(): String? = this?.takeIf { it.isNotBlank() }
