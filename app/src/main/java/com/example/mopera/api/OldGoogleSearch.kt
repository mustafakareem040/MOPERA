package com.example.mopera.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

object GoogleSearch {
    const val URL = "https://clients1.google.com/complete/search?hl=en&output=toolbar&q="
    private var client = HttpClient(OkHttp)
    private val regex = Regex("data=\"([^\"]*)")
    suspend fun suggestions(query: String): List<String> {
        if (query.isBlank()) return emptyList()
        try {
            val url = URL + query
            val response: HttpResponse = client.get(url)
            val body = response.bodyAsText()
            val matches = regex.findAll(body)
            val suggestionList = matches.map { it.groupValues[1] }.toList()
            return suggestionList
        }
        catch (e: Exception) {
            this.client = HttpClient(OkHttp)
            return emptyList()
        }

    }
}

