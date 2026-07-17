package org.oewntk.json.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class Client(
    val baseUrl: String
) {
    // THE ENGINE: Handled by passing CIO (or OkHttp) to HttpClient()
    val client = HttpClient(CIO) {

        // THE PLUGINS: Configured inside the install blocks
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }

        install(Logging) {
            logger = Logger.SIMPLE
            // - LogLevel.NONE: Silent
            // - LogLevel.INFO: Headers and status codes only
            // - LogLevel.BODY: Headers, status codes, AND the complete JSON body
            level = LogLevel.NONE // Keeps it clean and silent for production
        }
    }

    fun close() = client.close()

    // O B J E C T

    // THE CALLS: Clean suspend functions using the client
    suspend inline fun <reified T> fetchWithParam(path: String, parameterName: String, parameterValue: String): T {
        return client.get("$baseUrl$path") { parameter(parameterName, parameterValue) }
            .body()
    }

    suspend inline fun <reified T> fetch(path: String, parameter: String): T {
        return client.get("$baseUrl$path") { url { appendPathSegments(parameter) } }
            .body()
    }

    inline fun <reified T> query(path: String, parameter: String): T? = runBlocking {
        try {
            val fetched: T = fetch<T>(path, parameter)
            fetched
        } catch (e: Exception) {
            // Ktor propagates network and parsing exceptions up the stack
            System.err.println("[E] Failed to execute query: ${e.message}")
            null
        }
    }

    // T E X T

    suspend inline fun fetchText(path: String, parameter: String): String {
        return client.get("$baseUrl$path") { url { appendPathSegments(parameter) } }
            .bodyAsText()
    }

    fun queryText(path: String, parameter: String): String? = runBlocking {
        try {
            val fetched: String = fetchText(path, parameter)
            fetched
        } catch (e: Exception) {
            // Ktor propagates network and parsing exceptions up the stack
            System.err.println("[E] Failed to execute query: ${e.message}")
            null
        }
    }
}