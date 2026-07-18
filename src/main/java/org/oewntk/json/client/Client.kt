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

val prettyJson = Json {
    prettyPrint = true
    // Optional: prettyPrintIndent = "    " // Defaults to 4 spaces in recent versions
}

fun String.pretty(): String {
    val jsonElement = Json.parseToJsonElement(this)
    return prettyJson.encodeToString(jsonElement)
}

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
    suspend inline fun <reified T> fetch(path: String, parameter: String): T {
        return client.get("$baseUrl$path") {
            url { appendPathSegments(parameter) }
            // parameter(parameterName, parameterValue)
        }.body()
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

    suspend inline fun fetchText(path: String, parameter: String, options: String? = null): String {
        val response = client.get("$baseUrl$path") {
            url { appendPathSegments(parameter) }
            if (options != null) header(HttpHeaders.Prefer, options)
        }
        // Inspect if the backend subsystems honored your hint
        val appliedHints = response.headers[HttpHeaders.PreferenceApplied]
        // println("HTTP Status: ${response.status}")
        if (appliedHints != null) println("Server honored these preferences: $appliedHints")

        return response.bodyAsText()
    }

    fun queryText(path: String, parameter: String, options: String? = null): String? = runBlocking {
        try {
            val fetched: String = fetchText(path, parameter, options)
            fetched.pretty()
        } catch (e: Exception) {
            // Ktor propagates network and parsing exceptions up the stack
            System.err.println("[E] Failed to execute query: ${e.message}")
            null
        }
    }
}