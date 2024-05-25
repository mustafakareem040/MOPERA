package com.example.mopera.api

import android.annotation.SuppressLint
import com.example.mopera.data.MediaType
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import java.util.concurrent.TimeUnit


interface API<T> {
    companion object {
        val client: HttpClient by lazy {
            HttpClient(OkHttp) {
                engine {
                    config {
                        connectionPool(ConnectionPool(maxIdleConnections = 5, keepAliveDuration = 5, TimeUnit.MINUTES))
                        connectTimeout(30, TimeUnit.SECONDS)
                        readTimeout(30, TimeUnit.SECONDS)
                        writeTimeout(30, TimeUnit.SECONDS)
                        retryOnConnectionFailure(true)
                    }
                }
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
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