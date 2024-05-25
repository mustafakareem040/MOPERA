package com.example.mopera.api

import MovieDescription
import com.example.mopera.data.MediaType
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking


object MediaSearchResult: API<List<MovieDescription>> {
    override suspend fun fetch(query: String, mediaType: MediaType): List<MovieDescription> {
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
    suspend fun fetchMovies(title: String): List<MovieDescription> {
        try {
            val url =
                "https://cinemana.shabakaty.com/api/android/AdvancedSearch?level=1&videoTitle=${title}&staffTitle=${title}&page=0&year=1900,2024&type=movies"
            return API.client.get(url).body()
        }
        catch (e: Exception) {
            return emptyList()
        }
    }
    suspend fun fetchSeries(title: String): List<MovieDescription> {
        try {
            val url = "https://cinemana.shabakaty.com/api/android/AdvancedSearch?level=1&videoTitle=${title}&staffTitle=${title}&page=0&year=1900,2024&type=series"
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

