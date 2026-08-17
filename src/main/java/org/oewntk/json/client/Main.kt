/*
 * Copyright (c) 2021-2026. Bernard Bou.
 */
package org.oewntk.json.client

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.vararg
import org.oewntk.json.client.Args.inputTypeArg
import org.oewntk.json.client.Args.outputFormatArg
import org.oewntk.json.client.Args.outputModeArg
import org.oewntk.json.client.Client.Companion.log
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

    private const val LEMMA_CHAR_RE = "[a-zA-Z\\u00C0-\\u00D6\\u00D8-\\u00F60-9 \\-+.,:!/']"
    private const val SYNSET_RE = "\\d{8}-[nvars]"
    private const val LEMMA_RE = "$LEMMA_CHAR_RE+"
    private const val LEX_RE = "$LEMMA_RE,[nvars]-?\\d*"
    private const val SENSE_RE = "[a-zA-Z\\u00C0-\\u00D6\\u00D8-\\u00F60-9 \\-.+,!/':_]+%\\d+:\\d+:\\d+:$LEMMA_CHAR_RE*:\\d*"

    private fun classify(input: String): Args.InputType? {
        return when {
            input.matches("^$SYNSET_RE$".toRegex()) -> Args.InputType.SYNSET
            input.matches("^(?!$SYNSET_RE$)$LEX_RE$".toRegex()) -> Args.InputType.LEX
            input.matches("^(?!$SYNSET_RE$)$LEMMA_RE$".toRegex()) -> Args.InputType.WORD
            input.matches("^$SENSE_RE$".toRegex()) -> Args.InputType.SENSE
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
        val forcedInputType by parser.option(   inputTypeArg,          shortName = "t", fullName = "typed",          description = "Forced input type (synset, sense, lemma, lex, starts, contains, regex)")
        val outputMode by parser.option(        outputModeArg,         shortName = "r", fullName = "return",         description = "Return")         .default(Args.OutputMode.JSON)
        val outputFormat by parser.option(      outputFormatArg,       shortName = "f", fullName = "format",         description = "Format")         .default(Args.OutputFormat.OEWN)

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
            System.err.println("Schema: $outputFormat")
        }
        val client = Client(url)
        inputs.forEach { id ->
            val type = forcedInputType ?: (classify(id) ?: throw IllegalArgumentException("Untyped $id"))
            if (outputMode == Args.OutputMode.OBJECT) {

                when (type) {
                    Args.InputType.SYNSET -> {
                        client.query<Synset>("/api/synset/", id)?.let { println("[Synset] $it") }
                    }

                    Args.InputType.LEX -> {
                        client.query<Lex>("/api/lex/", id)?.let { println("[Lex] $it") }
                    }

                    Args.InputType.SENSE -> {
                        client.query<Sense>("/api/sense/", id)?.let { println("[Sense] $it") }
                    }

                    Args.InputType.WORD -> {
                        client.query<Collection<Lex>>("/api/word/", id)?.let { println("[Lexes] $it") }
                    }

                    Args.InputType.STARTS,
                    Args.InputType.CONTAINS,
                    Args.InputType.MATCHES -> IllegalArgumentException(type.toString())
                }
            } else {

                when (type) {
                    Args.InputType.SYNSET,
                    Args.InputType.LEX,
                    Args.InputType.SENSE,
                    Args.InputType.WORD -> {
                        val subpath = type.toString().lowercase()
                        client.queryText("/api/$subpath/", id, options = outputFormat.param)?.let {
                            println("[JSON '$id' $type $outputFormat]\n$it")
                            log(it, id, outputFormat.param, logTo)
                        }
                    }

                    Args.InputType.STARTS,
                    Args.InputType.CONTAINS,
                    Args.InputType.MATCHES -> {
                        val subpath = type.toString().lowercase()
                        client.queryText("/api/$subpath/", id, options = outputFormat.param)?.let {
                            println("[JSON '$id' $type $outputFormat]\n$it")
                        }
                    }
                }
            }
        }
    }
}
