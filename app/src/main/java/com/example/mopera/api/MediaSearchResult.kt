package com.example.mopera.api

import MediaSearchSuggestion
import com.example.mopera.data.MediaType
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import java.util.Calendar


object MediaSearchResult: API<List<MediaSearchSuggestion>> {
    // Search up to the current year, so new releases show up too.
    private fun currentYear() = Calendar.getInstance().get(Calendar.YEAR)

    override suspend fun fetch(query: String, mediaType: MediaType): List<MediaSearchSuggestion> {
        return try {
            if (mediaType == MediaType.MOVIE) {
                fetchMovies(query);
            } else {
                fetchSeries(query);
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun fetchMovies(title: String): List<MediaSearchSuggestion> {
        try {
            val url =
                "https://cinemana.shabakaty.com/api/android/AdvancedSearch?level=1&videoTitle=${title}&staffTitle=${title}&page=0&year=1900,${currentYear()}&type=movies"
            return API.client.get(url).body()
        }
        catch (e: Exception) {
            return emptyList()
        }
    }
    suspend fun fetchSeries(title: String): List<MediaSearchSuggestion> {
        try {
            val url = "https://cinemana.shabakaty.com/api/android/AdvancedSearch?level=1&videoTitle=${title}&staffTitle=${title}&page=0&year=1900,${currentYear()}&type=series"
            return API.client.get(url).body()
        }
        catch (e: Exception) {
            return emptyList()
        }
    }
}
fun main() = runBlocking {
    val movies = MediaSearchResult.fetchMovies("")
    println(movies)
}

