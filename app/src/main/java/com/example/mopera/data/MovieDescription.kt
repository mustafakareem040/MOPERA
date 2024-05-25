import com.example.mopera.data.MediaType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class MovieDescription(
    val nb: String,
    val ar_title: String,
    val en_title: String,
    val stars: String,
    val mDate: String,
    val year: String,
    @Serializable(with = MediaTypeSerializer::class) val kind: MediaType,
    val season: Int,
    val imgThumb: String,
    val imgMediumThumb: String,
    val imgObjUrl: String,
    val imgThumbObjUrl: String,
    val imgMediumThumbObjUrl: String,
    val seriesRating: String,
    val rate: String,
    val itemDate: String,
    val duration: Float,
)

object MediaTypeSerializer : KSerializer<MediaType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("MediaType", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: MediaType) {
        val intValue = when (value) {
            MediaType.MOVIE -> 1
            MediaType.SERIES -> 2
        }
        encoder.encodeInt(intValue)
    }

    override fun deserialize(decoder: Decoder): MediaType {
        return when (val intValue = decoder.decodeInt()) {
            1 -> MediaType.MOVIE
            2 -> MediaType.SERIES
            else -> throw IllegalArgumentException("Unknown MediaType value: $intValue")
        }
    }
}
