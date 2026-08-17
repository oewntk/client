/*
 * Copyright (c) 2021-2026. Bernard Bou.
 */
package org.oewntk.json.client

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.vararg
import org.oewntk.json.client.Args.oewnObjectArg
import org.oewntk.json.client.Args.optionsArg
import org.oewntk.json.client.Args.outputModeArg
import org.oewntk.model.Lex
import org.oewntk.model.Sense
import org.oewntk.model.Synset

/**
 * Main class that compares models
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Main {

    private const val lemmaCharRe = "[a-zA-Z\\u00C0-\\u00D6\\u00D8-\\u00F60-9 \\-+.,:!/']"
    private const val synsetRe = "\\d{8}-[nvars]"
    private const val lemmaRe = "$lemmaCharRe+"
    private const val lexRe = "$lemmaRe,[nvars]-?\\d*"
    private val senseRe = "[a-zA-Z\\u00C0-\\u00D6\\u00D8-\\u00F60-9 \\-.+,!/':_]+%\\d+:\\d+:\\d+:$lemmaCharRe*:\\d*"

    private fun classify(input: String): Args.OEWNObject? {
        return when {
            input.matches("^$synsetRe$".toRegex()) -> Args.OEWNObject.SYNSET
            input.matches("^(?!$synsetRe$)$lexRe$".toRegex()) -> Args.OEWNObject.LEX
            input.matches("^(?!$synsetRe$)$lemmaRe$".toRegex()) -> Args.OEWNObject.WORD
            input.matches("^$senseRe$".toRegex()) -> Args.OEWNObject.SENSE
            else -> null
        }
    }

    /**
     * Main entry point
     *
     * @param args command-line arguments
     * ```
     * yamlDir [outputDir]
     * ```
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val parser = ArgParser("client")
        // Options (start with - or --)
        // @formatter:off
        val inputs by parser.argument(          ArgType.String,                                                      description = "Query inputs")   .vararg()
        val forcedInputType by parser.option(   oewnObjectArg,         shortName = "t", fullName = "typed",          description = "Forced input type (synset, sense, lemma, lex)")
        val outputMode by parser.option(        outputModeArg,         shortName = "r", fullName = "return",         description = "Return")         .default(Args.ReturnType.JSON)
        val outputType by parser.option(        optionsArg,            shortName = "o", fullName = "output",         description = "Output")         .default(Args.OutputOptions.OEWN)

        val url by parser.option(               ArgType.String,        shortName = "u",  fullName = "url",           description = "URL")            .default("http://localhost:8080")
        val logTo by parser.option(             ArgType.String,        shortName = "l",  fullName = "log",           description = "Log")
        val verbose by parser.option(           ArgType.Boolean,       shortName = "v",  fullName = "verbose",       description = "Verbose output") .default(false)

        val traceTime by parser.option(         ArgType.Boolean,       shortName = "tt", fullName = "trace:time",    description = "trace time")     .default(false)
        val traceHeap by parser.option(         ArgType.Boolean,       shortName = "th", fullName = "trace:heap",    description = "trace heap")     .default(false)
        // @formatter:on

        parser.parse(args)
        if (verbose) {
            System.err.println("URL: $url")
            System.err.println("Inputs: $inputs")
            System.err.println("Input Type: $forcedInputType")
            System.err.println("Return: $outputMode")
            System.err.println("Schema: $outputType")
        }
        val client = Client(url)
        inputs.forEach { id ->
            val type = forcedInputType ?: (classify(id) ?: throw IllegalArgumentException("Untyped $id"))
            if (outputMode == Args.ReturnType.OBJECT) {

                when (type) {
                    Args.OEWNObject.SYNSET -> {
                        client.query<Synset>("/api/synset/", id)?.let { println("[Synset] $it") }
                    }

                    Args.OEWNObject.LEX -> {
                        client.query<Lex>("/api/lex/", id)?.let { println("[Lex] $it") }
                    }

                    Args.OEWNObject.SENSE -> {
                        client.query<Sense>("/api/sense/", id)?.let { println("[Sense] $it") }
                    }

                    Args.OEWNObject.WORD -> {
                        client.query<Collection<Lex>>("/api/word/", id)?.let { println("[Lexes] $it") }
                    }
                }
            } else {

                val subpath = type.toString().lowercase()
                client.queryText("/api/$subpath/", id, options = outputType.param)?.let {
                    println("[JSON '$id' $type $outputType]\n$it")
                    log(it, id, outputType.param, logTo)
                }
            }
        }
    }
}
