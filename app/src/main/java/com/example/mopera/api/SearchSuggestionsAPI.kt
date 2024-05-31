package com.example.mopera.api
import ApiResponse
import io.ktor.client.call.body
import io.ktor.client.request.get


object SearchSuggestions: API<List<String>> {
    private const val URL = "https://v3.sg.media-imdb.com/suggestion/titles/x/"

    override suspend fun fetch(query: String): List<String> {
        try {
            if (query.isEmpty()) return emptyList()
            val api = "$URL$query.json"
            val response: ApiResponse = API.client.get(api).body()
            return response.d.map { it.l }.distinct()
        }
        catch (e: Exception) {
            return emptyList()
        }
    }
}
