package org.oewntk.json.client

import org.junit.Test
import org.oewntk.json.client.Client.Companion.log
import org.oewntk.model.Lex
import org.oewntk.model.Sense
import org.oewntk.model.Synset

class TestClient {

    @Test
    fun queryObject() {
        val client = Client(url)
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

    @Test
    fun queryText() {
        val client = Client(url)
        client.queryText("/", "")?.let { println("[OEWN] $it") }

        listOf("00001740-a", "00001740-n", "00001740-r", "00001740-v")
            .forEach { id ->
                allOptions.forEach { options ->
                    client.queryText("/api/synset/", id, options = options)?.let {
                        println("[JSON SYNSET $options]\n$it")
                        log(it, id, options, logTo)
                    }
                }
            }

        listOf("row%1:14:00::", "row%1:17:00::", "row%1:06:00::", "row%1:14:01::", "row%1:07:00::", "row%1:04:00::", "row%1:10:00::", "row%2:38:00::")
            .forEach { id ->
                allOptions.forEach { options ->
                    client.queryText("/api/sense/", id, options = options)?.let {
                        println("[JSON SENSE $options]\n$it")
                        log(it, id, options, logTo)
                    }
                }
            }

        listOf("row,n-1", "row,n-2", "row,v")
            .forEach { id ->
                allOptions.forEach { options ->
                    client.queryText("/api/lex/", id, options = options)?.let {
                        println("[JSON LEX $options]\n$it")
                        log(it, id, options, logTo)
                    }
                }
            }

        listOf("row", "grow")
            .forEach { id ->
                allOptions.forEach { options ->
                    client.queryText("/api/word/", id, options = options)?.let {
                        println("[JSON LEMMA $options]\n$it")
                        log(it, id, options, logTo)
                    }
                }
            }
    }

    companion object {
        val url = System.getProperty("URL") ?: "http://localhost:8080"
        val logTo: String? = System.getProperty("LOG")
        val allOptions = listOf(null, "mode=model", "mode=oewn", "mode=data"/*, "mode=data,method=typed"*/)
    }
}