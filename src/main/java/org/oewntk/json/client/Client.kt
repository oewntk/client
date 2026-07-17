package org.oewntk.json.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.oewntk.model.Lex
import org.oewntk.model.Sense
import org.oewntk.model.Synset

object Client {

    // 1. THE ENGINE: Handled by passing CIO (or OkHttp) to HttpClient()
    val client = HttpClient(CIO) {

        // 2. THE PLUGINS: Configured inside the install blocks
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
            level = LogLevel.BODY // Keeps it clean and silent for production
        }
    }

    // 3. THE CALLS: Clean suspend functions using the client
    suspend inline fun <reified T> fetchWithParam(url: String, parameterName: String, parameterValue: String): T {
        return client.get(url) { parameter(parameterName, parameterValue) }
            .body()
    }

    suspend inline fun <reified T> fetch(url: String, parameter: String): T {
        return client.get(url) { url { appendPathSegments(parameter) } }
            .body()
    }

    inline fun <reified T> query(url: String, parameter: String): T? = runBlocking {
        try {
            val fetched: T = fetch<T>(url, parameter)
            fetched
        } catch (e: Exception) {
            // Ktor propagates network and parsing exceptions up the stack
            System.err.println("[E] Failed to execute query: ${e.message}")
            null
        }
    }

    val endpoint = "http://localhost:8080"

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        query<String>("$endpoint/", "")

        listOf("00001740-a", "00001740-n", "00001740-r", "00001740-v")
            .forEach {
                query<Synset>("$endpoint/api/synset/", it)?.let { println("[Fetched] $it") }
            }

        listOf("row%1:14:00::", "row%1:17:00::", "row%1:06:00::", "row%1:14:01::", "row%1:07:00::", "row%1:04:00::", "row%1:10:00::", "row%2:38:00::")
            .forEach {
                query<Sense>("$endpoint/api/sense/", it)?.let { println("[Fetched] $it") }
            }

        listOf("row,n-1", "row,n-2", "row,v")
            .forEach {
                query<Lex>("$endpoint/api/lex/", it)?.let { println("[Fetched] $it") }
            }

        listOf("row", "grow")
            .forEach {
                query<Collection<Lex>>("$endpoint/api/word/", it)?.let { println("[Fetched] $it") }
            }
    }
}