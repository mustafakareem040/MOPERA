import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val d: List<TitleItem>,
)

@Serializable
data class TitleItem(
    val l: String,
)

