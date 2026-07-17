package org.oewntk.json.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class RawJsonClient (
    private val baseUrl: String,
    private val client: HttpClient = HttpClient(CIO)
) : BaseClient() {
    suspend fun get(path: String): String =
        client.get("$baseUrl$path").bodyAsText()

    suspend fun post(path: String, jsonBody: String): String =
        client.post("$baseUrl$path") {
            setBody(jsonBody)
            header("Content-Type", "application/json")
        }.bodyAsText()

    fun close() = client.close()
}