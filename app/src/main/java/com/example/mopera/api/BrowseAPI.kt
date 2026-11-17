package com.example.mopera.api

import com.example.mopera.data.MediaType
import com.example.mopera.data.Video
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/** Newest movies and series, page by page (pages start at 0). */
object BrowseAPI {
    const val PAGE_SIZE = 24

    private const val BASE_URL = "https://cinemana.shabakaty.com/api/android"

    // Same content level the search screen asks for.
    private const val LEVEL = 1

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /** Throws on network errors so the caller can offer a retry. */
    suspend fun latest(type: MediaType, page: Int): List<Video> = withContext(Dispatchers.IO) {
        val endpoint = if (type == MediaType.MOVIE) "latestMovies" else "latestSeries"
        val response: JsonElement = API.client
            .get("$BASE_URL/$endpoint/level/$LEVEL/itemsPerPage/$PAGE_SIZE/page/$page/")
            .body()

        // The list normally comes bare; tolerate it being wrapped in an object too.
        val items = when (response) {
            is JsonArray -> response
            is JsonObject -> response.values.firstOrNull { it is JsonArray } as? JsonArray
            else -> null
        }.orEmpty()

        // The endpoint already says what kind of title this is, in case an item doesn't.
        val kind = if (type == MediaType.MOVIE) "1" else "2"
        items.mapNotNull { item ->
            runCatching { json.decodeFromJsonElement(Video.serializer(), item) }.getOrNull()
        }.map { if (it.kind.isNullOrBlank()) it.copy(kind = kind) else it }
    }
}
