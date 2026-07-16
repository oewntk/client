package io.github.oewntk.org.oewntk.json.client

import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.netty.handler.logging.LogLevel
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object MinimalApiClient {

    // 1. THE ENGINE: Handled by passing CIO (or OkHttp) to HttpClient()
    val client = HttpClient(CIO) {

        // 2. THE PLUGINS: Configured inside the install blocks
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            level = LogLevel.NONE // Keeps it clean and silent for production
        }
    }

    // 3. THE CALLS: Clean suspend functions using the client
    suspend inline fun <reified T> fetch(url: String): T {
        return client.get(url).body()
    }
}