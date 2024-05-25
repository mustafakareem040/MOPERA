package com.example.mopera.api

import com.example.mopera.data.SkippingDuration
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

object SkippingDurationAPI {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
    }
}
