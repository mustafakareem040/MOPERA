package com.example.mopera.api

import com.example.mopera.data.Tracks
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking


object TracksAPI: API<Tracks> {
    override suspend fun fetch(nb: Int): Tracks {
        val url = "https://cinemana.shabakaty.com/api/android/transcoddedFiles/id/$nb"
        return API.client.get(url).body()
    }
}
fun main() = runBlocking {
    val movies = TracksAPI.fetch(1147187)
    println(movies)
}
