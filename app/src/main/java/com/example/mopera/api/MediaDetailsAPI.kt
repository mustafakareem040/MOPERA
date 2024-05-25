package com.example.mopera.api

import com.example.mopera.data.MovieDetails
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

object MediaDetailsAPI: API<MovieDetails?> {
    override suspend fun fetch(nb: Int): MovieDetails? {
            val url = "https://cinemana.shabakaty.com/api/android/allVideoInfo/id/$nb"
        try {
            return API.client.get(url).body()
        }
        catch (e: Exception) {
            return null
        }
    }
}

fun main() = runBlocking {
    val movies = MediaDetailsAPI.fetch(343485)
    println(movies)
}

