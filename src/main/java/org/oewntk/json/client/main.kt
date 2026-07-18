package org.oewntk.json.client

import org.oewntk.model.Lex
import org.oewntk.model.Sense
import org.oewntk.model.Synset

const val endpoint = "http://localhost:8080"

fun queryObject() {
    val client = Client(endpoint)
    client.query<String>("/", "")

    listOf("00001740-a", "00001740-n", "00001740-r", "00001740-v")
        .forEach { id ->
            client.query<Synset>("/api/synset/", id)?.let { println("[Synset] $it") }
        }

    listOf("row%1:14:00::", "row%1:17:00::", "row%1:06:00::", "row%1:14:01::", "row%1:07:00::", "row%1:04:00::", "row%1:10:00::", "row%2:38:00::")
        .forEach { id ->
            client.query<Sense>("/api/sense/", id)?.let { println("[Sense] $it") }
        }

    listOf("row,n-1", "row,n-2", "row,v")
        .forEach { id ->
            client.query<Lex>("/api/lex/", id)?.let { println("[Lex] $it") }
        }

    listOf("row", "grow")
        .forEach { id ->
            client.query<Collection<Lex>>("/api/word/", id)?.let { println("[Lexes] $it") }
        }
}

fun queryText0() {
    val client = Client(endpoint)
    client.queryText("/", "")?.let { println("[OEWN] $it") }

    listOf("00001740-a", "00001740-n", "00001740-r", "00001740-v")
        .forEach { id ->
            client.queryText("/api/synset/", id, "mode=data, method=typed")?.let { println("[JSON SYNSET] $it") }
        }

    listOf("row%1:14:00::", "row%1:17:00::", "row%1:06:00::", "row%1:14:01::", "row%1:07:00::", "row%1:04:00::", "row%1:10:00::", "row%2:38:00::")
        .forEach { id ->
            client.queryText("/api/sense/", id)?.let { println("[JSON SENSE] $it") }
        }

    listOf("row,n-1", "row,n-2", "row,v")
        .forEach { id ->
            client.queryText("/api/lex/", id)?.let { println("[JSON LEX] $it") }
        }

    listOf("row", "grow")
        .forEach { id ->
            client.queryText("/api/word/", id)?.let { println("[JSON LEMMA] $it") }
        }
}

fun queryText() {
    val client = Client(endpoint)
    client.queryText("/", "")

    listOf("00001740-n")
        .forEach { id ->
            //client.queryText("/api/synset/", id)?.let { println("[JSON SYNSET] $it") }
            //client.queryText("/api/synset/", id, "mode=model")?.let { println("[JSON SYNSET] $it") }
            client.queryText("/api/synset/", id, "mode=data, method=typed")?.let { println("[JSON SYNSET] $it") }
            //client.queryText("/api/synset/", id, "mode=data")?.let { println("[JSON SYNSET] $it") }
            //client.queryText("/api/synset/", id, "mode=oewn")?.let { println("[JSON SYNSET] $it") }
        }

    //listOf("row%1:14:00::")
    //    .forEach { id ->
    //        client.queryText("/api/sense/", id)?.let { println("[JSON SENSE] $it") }
    //    }
//
    //listOf("row,n-1")
    //    .forEach { id ->
    //        client.queryText("/api/lex/", id)?.let { println("[JSON LEX] $it") }
    //    }
//
    //listOf("row")
    //    .forEach { id ->
    //        client.queryText("/api/word/", id)?.let { println("[JSON LEMMA] $it") }
    //    }
}

fun main(args: Array<String>) {
    //queryObject()
    queryText()
}