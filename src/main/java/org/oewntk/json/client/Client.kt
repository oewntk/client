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
            logger = Logger.DEFAULT
            // - LogLevel.NONE: Silent
            // - LogLevel.INFO: Headers and status codes only
            // - LogLevel.BODY: Headers, status codes, AND the complete JSON body
            level = LogLevel.BODY // Keeps it clean and silent for production
        }
    }

    // 3. THE CALLS: Clean suspend functions using the client
    suspend inline fun <reified T> fetch0(url: String, parameter: String): T {
        return client.get(url) { parameter("q", parameter) }
            .body()
    }

    suspend inline fun <reified T> fetch(url: String, parameter: String): T {
        return client.get(url) { url { appendPathSegments(parameter) } }
            .body()
    }

    suspend inline fun <reified T> fetch1(url: String, parameter: String): T {
        return client.get(url+parameter).body()
    }

    inline fun <reified T> query(url: String, parameter: String) = runBlocking {
        try {
            // Pass the target model type as the reified generic argument <TodoItem>
            val fetched: T = fetch<T>(url, parameter)

            // Use the parsed object safely
            println("[Fetched] $fetched")

        } catch (e: Exception) {
            // Ktor propagates network and parsing exceptions up the stack
            println("[E] Failed to execute query: ${e.message}")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val endpoint = "http://localhost:8080"
       //query<String>("$endpoint/", "")
        query<Synset>("$endpoint/api/synset/", "00001740-a")

        listOf("row%1:14:00::", "row%1:17:00::", "row%1:06:00::", "row%1:14:01::", "row%1:07:00::", "row%1:04:00::", "row%1:10:00::", "row%2:38:00::").forEach { sk ->
            query<Sense>("$endpoint/api/sense/", sk)
        }

        query<Lex>("$endpoint/api/lex/", "row,n-1")
        query<Collection<Lex>>("$endpoint/api/word/", "row")
    }
}