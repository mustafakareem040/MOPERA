package com.example.mopera.api

import com.example.mopera.data.VideoFormat
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

object VideoFormatAPI: API<List<VideoFormat>> {
    override suspend fun fetch(nb: Int): List<VideoFormat> {
        val response = API.client.get("https://cinemana.shabakaty.com/api/android/transcoddedFiles/id/$nb")
        return response.body()
    }
}
fun main() = runBlocking {
    val movies = VideoFormatAPI.fetch(1147187)
    println(movies)
}
