/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.json.client

import kotlinx.cli.ArgType

/**
 * Main class that generates the OEWN plus database
 *
 * @author Bernard Bou
 * @see "https://sqlunet.sourceforge.net/schema.html"
 */
object Args {

    enum class InputType {
        SYNSET,
        LEX,
        SENSE,
        WORD,

        STARTS,
        CONTAINS,
        MATCHES
    }

    val inputTypeArg = ArgType.Choice(
        choices = InputType.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "y", "synset" -> InputType.SYNSET
                "l", "lex" -> InputType.LEX
                "w", "lemma", "word" -> InputType.WORD
                "s", "sense" -> InputType.SENSE

                "ms", "starts" -> InputType.STARTS
                "mc", "contains" -> InputType.CONTAINS
                "mm", "matches" -> InputType.MATCHES

                else -> error("Unknown object type: $raw")
            }
        }
    )

    enum class OutputMode {
        OBJECT,
        JSON,
    }

    val outputModeArg = ArgType.Choice(
        choices = OutputMode.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "o", "object" -> OutputMode.OBJECT
                "j", "json" -> OutputMode.JSON
                else -> error("Unknown return type: $raw")
            }
        }
    )

    enum class OutputFormat(val param: String) {
        MODEL("mode=model"),
        OEWN("mode=oewn"),
        DATA("mode=data"),
        TYPED_DATA("mode=data,method=typed")
    }

    val outputFormatArg = ArgType.Choice(
        choices = OutputFormat.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "m", "model" -> OutputFormat.MODEL
                "o", "oewn" -> OutputFormat.OEWN
                "d", "data" -> OutputFormat.DATA
                "t", "typed" -> OutputFormat.TYPED_DATA
                else -> error("Unknown object type: $raw")
            }
        }
    )
}
