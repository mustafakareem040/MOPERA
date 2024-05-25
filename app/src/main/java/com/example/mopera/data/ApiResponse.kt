import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val d: List<TitleItem>,
    val q: String,
    val v: Int
)

@Serializable
data class TitleItem(
    val i: ImageInfo,
    val id: String,
    val l: String,
    val q: String,
    val qid: String,
    val rank: Int,
    val s: String,
    val y: Int,
    val yr: String? = null
)

@Serializable
data class ImageInfo(
    val height: Int,
    val imageUrl: String,
    val width: Int
)