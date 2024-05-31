package com.example.mopera.api

import com.example.mopera.data.Episode
import com.example.mopera.data.MediaType
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking

object EpisodesAPI: API<Map<Int, List<Episode>>> {
    suspend fun parse(nb: Int): List<Episode> {
        val url = "https://cinemana.shabakaty.com/api/android/videoSeason/id/${nb}"
        return API.client.get(url).body()
    }

    override suspend fun fetch(nb: Int): Map<Int, List<Episode>> {
        return parse(nb).groupBy { it.season }
    }
}
