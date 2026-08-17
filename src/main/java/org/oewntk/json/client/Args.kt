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

    enum class ReturnType {
        OBJECT,
        JSON,
    }

    val outputModeArg = ArgType.Choice(
        choices = ReturnType.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "o", "object" -> ReturnType.OBJECT
                "j", "json" -> ReturnType.JSON
                else -> error("Unknown return type: $raw")
            }
        }
    )

    enum class OEWNObject {
        SYNSET,
        LEX,
        SENSE,
        WORD
    }

    val oewnObjectArg = ArgType.Choice(
        choices = OEWNObject.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "y", "synset" -> OEWNObject.SYNSET
                "l", "lex" -> OEWNObject.LEX
                "w", "lemma", "word" -> OEWNObject.WORD
                "s", "sense" -> OEWNObject.SENSE
                else -> error("Unknown object type: $raw")
            }
        }
    )

    enum class OutputOptions(val param: String) {
        MODEL("mode=model"),
        OEWN("mode=oewn"),
        DATA("mode=data"),
        TYPED_DATA("mode=data,method=typed")
    }

    val optionsArg = ArgType.Choice(
        choices = OutputOptions.entries,
        variantToString = { it.name.lowercase() },
        toVariant = { raw ->
            when (raw.lowercase()) {
                "m", "model" -> OutputOptions.MODEL
                "o", "oewn" -> OutputOptions.OEWN
                "d", "data" -> OutputOptions.DATA
                "t", "typed" -> OutputOptions.TYPED_DATA
                else -> error("Unknown object type: $raw")
            }
        }
    )

}
