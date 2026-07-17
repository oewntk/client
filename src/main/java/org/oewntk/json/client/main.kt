package org.oewntk.json.client

import org.oewntk.model.Lex
import org.oewntk.model.Sense
import org.oewntk.model.Synset

val endpoint = "http://localhost:8080"

fun queryObject() {
    val client = Client()
    client.query<String>("$endpoint/", "")

    listOf("00001740-a", "00001740-n", "00001740-r", "00001740-v")
        .forEach {
            client.query<Synset>("$endpoint/api/synset/", it)?.let { println("[Fetched] $it") }
        }

    listOf("row%1:14:00::", "row%1:17:00::", "row%1:06:00::", "row%1:14:01::", "row%1:07:00::", "row%1:04:00::", "row%1:10:00::", "row%2:38:00::")
        .forEach {
            client.query<Sense>("$endpoint/api/sense/", it)?.let { println("[Fetched] $it") }
        }

    listOf("row,n-1", "row,n-2", "row,v")
        .forEach {
            client.query<Lex>("$endpoint/api/lex/", it)?.let { println("[Fetched] $it") }
        }

    listOf("row", "grow")
        .forEach {
            client.query<Collection<Lex>>("$endpoint/api/word/", it)?.let { println("[Fetched] $it") }
        }
}

fun queryText() {
    val client = Client()
    client.queryText("$endpoint/", "")

    listOf("00001740-a", "00001740-n", "00001740-r", "00001740-v")
        .forEach {
            client.queryText("$endpoint/api/synset/", it)?.let { println("[Fetched] $it") }
        }

    listOf("row%1:14:00::", "row%1:17:00::", "row%1:06:00::", "row%1:14:01::", "row%1:07:00::", "row%1:04:00::", "row%1:10:00::", "row%2:38:00::")
        .forEach {
            client.queryText("$endpoint/api/sense/", it)?.let { println("[Fetched] $it") }
        }

    listOf("row,n-1", "row,n-2", "row,v")
        .forEach {
            client.queryText("$endpoint/api/lex/", it)?.let { println("[Fetched] $it") }
        }

    listOf("row", "grow")
        .forEach {
            client.queryText("$endpoint/api/word/", it)?.let { println("[Fetched] $it") }
        }
}

fun main(args: Array<String>) {
    queryObject()
    queryText()
}