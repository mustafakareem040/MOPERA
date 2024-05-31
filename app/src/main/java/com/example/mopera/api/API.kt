package com.example.mopera.api

import com.example.mopera.data.MediaType
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.Protocol
import java.util.concurrent.TimeUnit

interface API<T> {
    companion object {
        val client: HttpClient by lazy {
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }
        }
    }

    @Deprecated("This method is not implemented yet.", level = DeprecationLevel.ERROR)
    suspend fun fetch(query: String): T {
        throw NotImplementedError()
    }

    @Deprecated("This method is not implemented yet.", level = DeprecationLevel.ERROR)
    suspend fun fetch(nb: Int): T {
        throw NotImplementedError()
    }

    @Deprecated("This method is not implemented yet.", level = DeprecationLevel.ERROR)
    suspend fun fetch(query: String, mediaType: MediaType): T {
        throw NotImplementedError()
    }
}
